package com.test.tool;

import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

public class ProduceFactory {
    public static KafkaProducer getKafkaProducer(){
        Properties properties = new Properties();

//        properties.put("bootstrap.servers", "172.31.24.30:9092,172.31.24.36:9092");
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.31.35.58:9092");
//        properties.put("bootstrap.servers", "172.31.24.36:9092");
//        properties.put("bootstrap.servers", "192.168.222.80:9092,192.168.222.81:9092,192.168.222.82:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 1);
        /**
         *
         */
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer producer = new KafkaProducer<String, String>(properties);
        return producer;
    }
}
