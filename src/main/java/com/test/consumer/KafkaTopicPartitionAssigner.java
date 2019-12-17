package com.test.consumer;

public class KafkaTopicPartitionAssigner {
    public static int assign(KafkaTopicPartition partition, int numParallelSubtasks) {
        int startIndex = ((partition.getTopic().hashCode() * 31) & 0x7FFFFFFF) % numParallelSubtasks;

        // here, the assumption is that the id of Kafka partitions are always ascending
        // starting from 0, and therefore can be used directly as the offset clockwise from the start index
        return (startIndex + partition.getPartition()) % numParallelSubtasks;
    }

    public static void main(String[] args) {

    }
}
