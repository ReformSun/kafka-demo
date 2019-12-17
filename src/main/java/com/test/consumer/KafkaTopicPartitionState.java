package com.test.consumer;

public class KafkaTopicPartitionState<KPH> {

    // ------------------------------------------------------------------------

    /** The Flink description of a Kafka partition. */
    private final KafkaTopicPartition partition;

    /** The Kafka description of a Kafka partition (varies across different Kafka versions). */
    private final KPH kafkaPartitionHandle;

    /** The offset within the Kafka partition that we already processed. */
    private volatile long offset;

    /** The offset of the Kafka partition that has been committed. */
    private volatile long committedOffset;

    // ------------------------------------------------------------------------

    public KafkaTopicPartitionState(KafkaTopicPartition partition, KPH kafkaPartitionHandle) {
        this.partition = partition;
        this.kafkaPartitionHandle = kafkaPartitionHandle;
        this.offset = KafkaTopicPartitionStateSentinel.OFFSET_NOT_SET;
        this.committedOffset = KafkaTopicPartitionStateSentinel.OFFSET_NOT_SET;
    }

    // ------------------------------------------------------------------------

    /**
     * Gets Flink's descriptor for the Kafka Partition.
     * @return The Flink partition descriptor.
     */
    public final KafkaTopicPartition getKafkaTopicPartition() {
        return partition;
    }

    /**
     * Gets Kafka's descriptor for the Kafka Partition.
     * @return The Kafka partition descriptor.
     */
    public final KPH getKafkaPartitionHandle() {
        return kafkaPartitionHandle;
    }

    public final String getTopic() {
        return partition.getTopic();
    }

    public final int getPartition() {
        return partition.getPartition();
    }

    /**
     * The current offset in the partition. This refers to the offset last element that
     * we retrieved and emitted successfully. It is the offset that should be stored in
     * a checkpoint.
     */
    public final long getOffset() {
        return offset;
    }

    public final void setOffset(long offset) {
        this.offset = offset;
    }

    public final boolean isOffsetDefined() {
        return offset != KafkaTopicPartitionStateSentinel.OFFSET_NOT_SET;
    }

    public final void setCommittedOffset(long offset) {
        this.committedOffset = offset;
    }

    public final long getCommittedOffset() {
        return committedOffset;
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Partition: " + partition + ", KafkaPartitionHandle=" + kafkaPartitionHandle
                + ", offset=" + (isOffsetDefined() ? String.valueOf(offset) : "(not set)");
    }
}

