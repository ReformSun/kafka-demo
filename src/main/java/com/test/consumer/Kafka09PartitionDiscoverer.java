package com.test.consumer;

import com.test.tool.ConsumerFactory;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static com.sun.util.Preconditions.checkNotNull;

public class Kafka09PartitionDiscoverer extends AbstractPartitionDiscoverer{

    public static void main(String[] args) throws Exception {
        Properties properties = ConsumerFactory.getProperties();
        List<String> list = new ArrayList(){{
            add("963672");
        }};

        KafkaTopicsDescriptor kafkaTopicsDescriptor = new KafkaTopicsDescriptor(list,null);

        Kafka09PartitionDiscoverer kafka09PartitionDiscoverer = new Kafka09PartitionDiscoverer(kafkaTopicsDescriptor,1,1,properties);

        kafka09PartitionDiscoverer.open();

        List<KafkaTopicPartition> kafkaTopicPartitions = kafka09PartitionDiscoverer.discoverPartitions();

        System.out.println(kafkaTopicPartitions.size());



    }
        private final Properties kafkaProperties;

        private KafkaConsumer<?, ?> kafkaConsumer;

    /**
     * @param topicsDescriptor
     * @param indexOfThisSubtask 当前子任务的索引值
     * @param numParallelSubtasks 总的子分区数
     * @param kafkaProperties
     */
        public Kafka09PartitionDiscoverer(
                KafkaTopicsDescriptor topicsDescriptor,
                int indexOfThisSubtask,
                int numParallelSubtasks,
                Properties kafkaProperties) {

            super(topicsDescriptor, indexOfThisSubtask, numParallelSubtasks);
            this.kafkaProperties = checkNotNull(kafkaProperties);
        }

        @Override
        protected void initializeConnections() {
            this.kafkaConsumer = new KafkaConsumer<>(kafkaProperties);
        }

        @Override
        protected List<String> getAllTopics() throws WakeupException {
            try {
                return new ArrayList<>(kafkaConsumer.listTopics().keySet());
            } catch (org.apache.kafka.common.errors.WakeupException e) {
                // rethrow our own wakeup exception
                throw new WakeupException();
            }
        }

        @Override
        protected List<KafkaTopicPartition> getAllPartitionsForTopics(List<String> topics) throws WakeupException {
            List<KafkaTopicPartition> partitions = new LinkedList<>();

            try {
                for (String topic : topics) {
                    for (PartitionInfo partitionInfo : kafkaConsumer.partitionsFor(topic)) {
                        partitions.add(new KafkaTopicPartition(partitionInfo.topic(), partitionInfo.partition()));
                    }
                }
            } catch (org.apache.kafka.common.errors.WakeupException e) {
                // rethrow our own wakeup exception
                throw new WakeupException();
            }

            return partitions;
        }

        @Override
        protected void wakeupConnections() {
            if (this.kafkaConsumer != null) {
                this.kafkaConsumer.wakeup();
            }
        }

        @Override
        protected void closeConnections() throws Exception {
            if (this.kafkaConsumer != null) {
                this.kafkaConsumer.close();

                // de-reference the consumer to avoid closing multiple times
                this.kafkaConsumer = null;
            }
        }
}
