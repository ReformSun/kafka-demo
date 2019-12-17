package com.test.consumer;

import com.test.tool.FileWriter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

public class KafkaFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaFetcher.class);
    private final Handover handover;
    private final KafkaConsumerThread consumerThread;
    private volatile boolean running = true;
    private final List<KafkaTopicPartitionState<TopicPartition>> subscribedPartitionStates;


    public static void main(String[] args) {
        KafkaFetcher kafkaFetcher = new KafkaFetcher();
        try {
            kafkaFetcher.runFetchLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KafkaFetcher() {
        this.handover = new Handover();
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "172.31.35.58:9092");
        properties.put("group.id", "ddddd");
        // 启动偏移量自动提交服务
        properties.put("enable.auto.commit", "true");
        // 设置偏移量自动提交时间间隔10秒钟
        properties.put("auto.commit.interval.ms", "10000");
        // 设置偏移量消费设置 最早
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue = new ClosableBlockingQueue<>();

        TopicPartition topicPartition = new TopicPartition("963673",0);
        KafkaTopicPartition kafkaTopicPartition = new KafkaTopicPartition("963672",0);
        KafkaTopicPartitionState kafkaTopicPartitionState = new KafkaTopicPartitionState(kafkaTopicPartition,topicPartition);
        kafkaTopicPartitionState.setOffset(5);
        unassignedPartitionsQueue.add(kafkaTopicPartitionState);



        this.consumerThread = new KafkaConsumerThread(LOG,handover
                ,properties
                ,unassignedPartitionsQueue
                ,new KafkaConsumerCallBridge()
                ,"test",1000);
        this.subscribedPartitionStates = new ArrayList<>();
        this.subscribedPartitionStates.add(kafkaTopicPartitionState);
    }

    public void runFetchLoop() throws Exception {
        try {
            final Handover handover = this.handover;

            // kick off the actual Kafka consumer
            consumerThread.start();

            while (running) {
                // this blocks until we get the next records
                // it automatically re-throws exceptions encountered in the consumer thread
                final ConsumerRecords<byte[], byte[]> records = handover.pollNext();

                // get the records for each topic partition
                for (KafkaTopicPartitionState<TopicPartition> partition : subscribedPartitionStates) {

                    List<ConsumerRecord<byte[], byte[]>> partitionRecords =
                            records.records(partition.getKafkaPartitionHandle());

                    for (ConsumerRecord<byte[], byte[]> record : partitionRecords) {

                        FileWriter.writerFile(record.topic() + ":" + record.offset());
//                        final T value = deserializer.deserialize(
//                                record.key(), record.value(),
//                                record.topic(), record.partition(), record.offset());

//                        if (deserializer.isEndOfStream(value)) {
//                            // end of stream signaled
//                            running = false;
//                            break;
//                        }

                        // emit the actual record. this also updates offset state atomically
                        // and deals with timestamps and watermark generation
//                        emitRecord(value, partition, record.offset(), record);
                    }
                }
            }
        }
        finally {
            // this signals the consumer thread that no more work is to be done
            consumerThread.shutdown();
        }

        // on a clean exit, wait for the runner thread
        try {
            consumerThread.join();
        }
        catch (InterruptedException e) {
            // may be the result of a wake-up interruption after an exception.
            // we ignore this here and only restore the interruption state
            Thread.currentThread().interrupt();
        }
    }

    public void doCommitInternalOffsetsToKafka(
            Map<KafkaTopicPartition, Long> offsets,
            @Nonnull KafkaCommitCallback commitCallback) throws Exception {

        @SuppressWarnings("unchecked")
        List<KafkaTopicPartitionState<TopicPartition>> partitions = subscribedPartitionStates;

        Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>(partitions.size());

        for (KafkaTopicPartitionState<TopicPartition> partition : partitions) {
            Long lastProcessedOffset = offsets.get(partition.getKafkaTopicPartition());
            if (lastProcessedOffset != null) {
                // committed offsets through the KafkaConsumer need to be 1 more than the last processed offset.
                // This does not affect Flink's checkpoints/saved state.
                long offsetToCommit = lastProcessedOffset + 1;

                offsetsToCommit.put(partition.getKafkaPartitionHandle(), new OffsetAndMetadata(offsetToCommit));
                partition.setCommittedOffset(offsetToCommit);
            }
        }

        // record the work to be committed by the main consumer thread and make sure the consumer notices that
        consumerThread.setOffsetsToCommit(offsetsToCommit, commitCallback);
    }

    public void cancel() {
        // flag the main thread to exit. A thread interrupt will come anyways.
        running = false;
        handover.close();
        consumerThread.shutdown();
    }
}
