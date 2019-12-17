package com.test.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.List;

public class KafkaConsumerCallBridge {
    public void assignPartitions(KafkaConsumer<?, ?> consumer, List<TopicPartition> topicPartitions) throws Exception {
        consumer.assign(topicPartitions);
    }

    public void seekPartitionToBeginning(KafkaConsumer<?, ?> consumer, TopicPartition partition) {
        consumer.seekToBeginning(Arrays.asList(partition));
    }

    public void seekPartitionToEnd(KafkaConsumer<?, ?> consumer, TopicPartition partition) {
        consumer.seekToEnd(Arrays.asList(partition));
    }
}
