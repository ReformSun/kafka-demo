package com.test.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.sun.util.Preconditions.checkNotNull;

public class KafkaConsumerThread extends Thread{
    private final Logger log;
    private final Handover handover;
    private final AtomicReference<Tuple2<Map<TopicPartition, OffsetAndMetadata>, KafkaCommitCallback>> nextOffsetsToCommit;
    private final ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue;
    private final Properties kafkaProperties;
    private final long pollTimeout;
    private final KafkaConsumerCallBridge consumerCallBridge;
    private volatile KafkaConsumer<byte[], byte[]> consumer;
    private final Object consumerReassignmentLock;
    private boolean hasAssignedPartitions;
    private volatile boolean hasBufferedWakeup;
    private volatile boolean running;
    private volatile boolean commitInProgress;
    public KafkaConsumerThread(
            Logger log,
            Handover handover,
            Properties kafkaProperties,
            ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue,
            KafkaConsumerCallBridge consumerCallBridge,
            String threadName,
            long pollTimeout){
        super(threadName);
        this.log = log;
        this.handover = handover;
        this.kafkaProperties = kafkaProperties;
        this.consumerCallBridge = consumerCallBridge;
        this.pollTimeout = pollTimeout;
        this.unassignedPartitionsQueue = checkNotNull(unassignedPartitionsQueue);
        this.consumerReassignmentLock = new Object();
        this.nextOffsetsToCommit = new AtomicReference<>();
        this.running = true;

    }



    @Override
    public void run() {
        // early exit check
        if (!running) {
            return;
        }

        // this is the means to talk to FlinkKafkaConsumer's main thread
        final Handover handover = this.handover;

        // This method initializes the KafkaConsumer and guarantees it is torn down properly.
        // This is important, because the consumer has multi-threading issues,
        // including concurrent 'close()' calls.
        try {
            this.consumer = getConsumer(kafkaProperties);
        }
        catch (Throwable t) {
            handover.reportError(t);
            return;
        }

        // from here on, the consumer is guaranteed to be closed properly
        try {
            // register Kafka's very own metrics in Flink's metric reporters

            // early exit check
            if (!running) {
                return;
            }

            // the latest bulk of records. May carry across the loop if the thread is woken up
            // from blocking on the handover
            ConsumerRecords<byte[], byte[]> records = null;

            // reused variable to hold found unassigned new partitions.
            // found partitions are not carried across loops using this variable;
            // they are carried across via re-adding them to the unassigned partitions queue
            List<KafkaTopicPartitionState<TopicPartition>> newPartitions;

            // main fetch loop
            while (running) {

                // check if there is something to commit
                if (!commitInProgress) {
                    // get and reset the work-to-be committed, so we don't repeatedly commit the same
                    final Tuple2<Map<TopicPartition, OffsetAndMetadata>, KafkaCommitCallback> commitOffsetsAndCallback =
                            nextOffsetsToCommit.getAndSet(null);

                    if (commitOffsetsAndCallback != null) {
                        log.debug("Sending async offset commit request to Kafka broker");

                        // also record that a commit is already in progress
                        // the order here matters! first set the flag, then send the commit command.
                        commitInProgress = true;
                        consumer.commitAsync(commitOffsetsAndCallback.f0, new CommitCallback(commitOffsetsAndCallback.f1));
                    }
                }

                try {
                    if (hasAssignedPartitions) {
                        newPartitions = unassignedPartitionsQueue.pollBatch();
                    }
                    else {
                        // if no assigned partitions block until we get at least one
                        // instead of hot spinning this loop. We rely on a fact that
                        // unassignedPartitionsQueue will be closed on a shutdown, so
                        // we don't block indefinitely
                        newPartitions = unassignedPartitionsQueue.getBatchBlocking();
                    }
                    if (newPartitions != null) {
                        reassignPartitions(newPartitions);
                    }
                } catch (AbortedReassignmentException e) {
                    continue;
                }

                if (!hasAssignedPartitions) {
                    // Without assigned partitions KafkaConsumer.poll will throw an exception
                    continue;
                }

                // get the next batch of records, unless we did not manage to hand the old batch over
                if (records == null) {
                    try {
                        records = consumer.poll(pollTimeout);
                    }
                    catch (WakeupException we) {
                        continue;
                    }
                }

                try {
                    handover.produce(records);
                    records = null;
                }
                catch (Handover.WakeupException e) {
                    // fall through the loop
                }
            }
            // end main fetch loop
        }
        catch (Throwable t) {
            // let the main thread know and exit
            // it may be that this exception comes because the main thread closed the handover, in
            // which case the below reporting is irrelevant, but does not hurt either
            handover.reportError(t);
        }
        finally {
            // make sure the handover is closed if it is not already closed or has an error
            handover.close();

            // make sure the KafkaConsumer is closed
            try {
                consumer.close();
            }
            catch (Throwable t) {
                log.warn("Error while closing Kafka consumer", t);
            }
        }
    }

    public void shutdown() {
        running = false;

        // wake up all blocking calls on the queue
        unassignedPartitionsQueue.close();

        // We cannot call close() on the KafkaConsumer, because it will actually throw
        // an exception if a concurrent call is in progress

        // this wakes up the consumer if it is blocked handing over records
        handover.wakeupProducer();

        // this wakes up the consumer if it is blocked in a kafka poll
        synchronized (consumerReassignmentLock) {
            if (consumer != null) {
                consumer.wakeup();
            } else {
                // the consumer is currently isolated for partition reassignment;
                // set this flag so that the wakeup state is restored once the reassignment is complete
                hasBufferedWakeup = true;
            }
        }
    }

    void setOffsetsToCommit(
            Map<TopicPartition, OffsetAndMetadata> offsetsToCommit,
            @Nonnull KafkaCommitCallback commitCallback) {

        // record the work to be committed by the main consumer thread and make sure the consumer notices that
        if (nextOffsetsToCommit.getAndSet(Tuple2.of(offsetsToCommit, commitCallback)) != null) {
            log.warn("Committing offsets to Kafka takes longer than the checkpoint interval. " +
                    "Skipping commit of previous offsets because newer complete checkpoint offsets are available. " +
                    "This does not compromise Flink's checkpoint integrity.");
        }

        // if the consumer is blocked in a poll() or handover operation, wake it up to commit soon
        handover.wakeupProducer();

        synchronized (consumerReassignmentLock) {
            if (consumer != null) {
                consumer.wakeup();
            } else {
                // the consumer is currently isolated for partition reassignment;
                // set this flag so that the wakeup state is restored once the reassignment is complete
                hasBufferedWakeup = true;
            }
        }
    }

    void reassignPartitions(List<KafkaTopicPartitionState<TopicPartition>> newPartitions) throws Exception {
        if (newPartitions.size() == 0) {
            return;
        }
        hasAssignedPartitions = true;
        boolean reassignmentStarted = false;

        // since the reassignment may introduce several Kafka blocking calls that cannot be interrupted,
        // the consumer needs to be isolated from external wakeup calls in setOffsetsToCommit() and shutdown()
        // until the reassignment is complete.
        final KafkaConsumer<byte[], byte[]> consumerTmp;
        synchronized (consumerReassignmentLock) {
            consumerTmp = this.consumer;
            this.consumer = null;
        }

        final Map<TopicPartition, Long> oldPartitionAssignmentsToPosition = new HashMap<>();
        try {
            for (TopicPartition oldPartition : consumerTmp.assignment()) {
                oldPartitionAssignmentsToPosition.put(oldPartition, consumerTmp.position(oldPartition));
            }

            final List<TopicPartition> newPartitionAssignments =
                    new ArrayList<>(newPartitions.size() + oldPartitionAssignmentsToPosition.size());
            newPartitionAssignments.addAll(oldPartitionAssignmentsToPosition.keySet());
            newPartitionAssignments.addAll(convertKafkaPartitions(newPartitions));

            // reassign with the new partitions
            consumerCallBridge.assignPartitions(consumerTmp, newPartitionAssignments);
            reassignmentStarted = true;

            // old partitions should be seeked to their previous position
            for (Map.Entry<TopicPartition, Long> oldPartitionToPosition : oldPartitionAssignmentsToPosition.entrySet()) {
                consumerTmp.seek(oldPartitionToPosition.getKey(), oldPartitionToPosition.getValue());
            }

            // offsets in the state of new partitions may still be placeholder sentinel values if we are:
            //   (1) starting fresh,
            //   (2) checkpoint / savepoint state we were restored with had not completely
            //       been replaced with actual offset values yet, or
            //   (3) the partition was newly discovered after startup;
            // replace those with actual offsets, according to what the sentinel value represent.
            for (KafkaTopicPartitionState<TopicPartition> newPartitionState : newPartitions) {
                if (newPartitionState.getOffset() == KafkaTopicPartitionStateSentinel.EARLIEST_OFFSET) {
                    consumerCallBridge.seekPartitionToBeginning(consumerTmp, newPartitionState.getKafkaPartitionHandle());
                    newPartitionState.setOffset(consumerTmp.position(newPartitionState.getKafkaPartitionHandle()) - 1);
                } else if (newPartitionState.getOffset() == KafkaTopicPartitionStateSentinel.LATEST_OFFSET) {
                    consumerCallBridge.seekPartitionToEnd(consumerTmp, newPartitionState.getKafkaPartitionHandle());
                    newPartitionState.setOffset(consumerTmp.position(newPartitionState.getKafkaPartitionHandle()) - 1);
                } else if (newPartitionState.getOffset() == KafkaTopicPartitionStateSentinel.GROUP_OFFSET) {
                    // the KafkaConsumer by default will automatically seek the consumer position
                    // to the committed group offset, so we do not need to do it.

                    newPartitionState.setOffset(consumerTmp.position(newPartitionState.getKafkaPartitionHandle()) - 1);
                } else {
                    consumerTmp.seek(newPartitionState.getKafkaPartitionHandle(), newPartitionState.getOffset() + 1);
                }
            }
        } catch (WakeupException e) {
            // a WakeupException may be thrown if the consumer was invoked wakeup()
            // before it was isolated for the reassignment. In this case, we abort the
            // reassignment and just re-expose the original consumer.

            synchronized (consumerReassignmentLock) {
                this.consumer = consumerTmp;

                // if reassignment had already started and affected the consumer,
                // we do a full roll back so that it is as if it was left untouched
                if (reassignmentStarted) {
                    consumerCallBridge.assignPartitions(
                            this.consumer, new ArrayList<>(oldPartitionAssignmentsToPosition.keySet()));

                    for (Map.Entry<TopicPartition, Long> oldPartitionToPosition : oldPartitionAssignmentsToPosition.entrySet()) {
                        this.consumer.seek(oldPartitionToPosition.getKey(), oldPartitionToPosition.getValue());
                    }
                }

                // no need to restore the wakeup state in this case,
                // since only the last wakeup call is effective anyways
                hasBufferedWakeup = false;

                // re-add all new partitions back to the unassigned partitions queue to be picked up again
                for (KafkaTopicPartitionState<TopicPartition> newPartition : newPartitions) {
                    unassignedPartitionsQueue.add(newPartition);
                }

                // this signals the main fetch loop to continue through the loop
//                throw new AbortedReassignmentException();
            }
        }

        // reassignment complete; expose the reassigned consumer
        synchronized (consumerReassignmentLock) {
            this.consumer = consumerTmp;

            // restore wakeup state for the consumer if necessary
            if (hasBufferedWakeup) {
                this.consumer.wakeup();
                hasBufferedWakeup = false;
            }
        }
    }
    KafkaConsumer<byte[], byte[]> getConsumer(Properties kafkaProperties) {
        return new KafkaConsumer<>(kafkaProperties);
    }

    private static List<TopicPartition> convertKafkaPartitions(List<KafkaTopicPartitionState<TopicPartition>> partitions) {
        ArrayList<TopicPartition> result = new ArrayList<>(partitions.size());
        for (KafkaTopicPartitionState<TopicPartition> p : partitions) {
            result.add(p.getKafkaPartitionHandle());
        }
        return result;
    }

    private class CommitCallback implements OffsetCommitCallback {

        private final KafkaCommitCallback internalCommitCallback;

        CommitCallback(KafkaCommitCallback internalCommitCallback) {
            this.internalCommitCallback = checkNotNull(internalCommitCallback);
        }

        @Override
        public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception ex) {
            commitInProgress = false;

            if (ex != null) {
                log.warn("Committing offsets to Kafka failed. This does not compromise Flink's checkpoints.", ex);
                internalCommitCallback.onException(ex);
            } else {
                internalCommitCallback.onSuccess();
            }
        }
    }

    /**
     * Utility exception that serves as a signal for the main loop to continue through the loop
     * if a reassignment attempt was aborted due to an pre-reassignment wakeup call on the consumer.
     */
    private static class AbortedReassignmentException extends Exception {
        private static final long serialVersionUID = 1L;
    }

}
