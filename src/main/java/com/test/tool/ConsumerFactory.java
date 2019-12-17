package com.test.tool;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

public class ConsumerFactory {
    public static KafkaConsumer getKafkaConsumer(){
        KafkaConsumer<String, String> kafkaConsumer = getKafkaConsumer("ssssss");
        return kafkaConsumer;
    }

    public static KafkaConsumer getKafkaConsumer(String groupId){
        Properties properties = new Properties();
//        properties.put("bootstrap.servers", "172.31.24.30:9092,172.31.24.36:9092");
//        properties.put("bootstrap.servers", "172.31.35.58:9092");
//        properties.put("bootstrap.servers", "172.31.35.23:9092");
//        properties.put("bootstrap.servers", "172.31.24.36:9092");
//        properties.put("bootstrap.servers", "192.168.222.80:9092,192.168.222.81:9092,192.168.222.82:9092");
        properties.put("bootstrap.servers", "192.168.223.100:9092,192.168.223.101:9092,192.168.223.102:9092");
        properties.put("group.id", groupId);
        // 启动偏移量自动提交服务
        properties.put("enable.auto.commit", "true");
        // 设置偏移量自动提交时间间隔10秒钟
        properties.put("auto.commit.interval.ms", "10000");
        // 设置偏移量消费设置 最早
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");


        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer(properties);
        return kafkaConsumer;
    }

    public static Properties getProperties(){
        Properties properties = new Properties();
//        properties.put("bootstrap.servers", "172.31.24.30:9092,172.31.24.36:9092");
        properties.put("bootstrap.servers", "172.31.35.58:9092");
//        properties.put("bootstrap.servers", "172.31.35.23:9092");
//        properties.put("bootstrap.servers", "172.31.24.36:9092");
//        properties.put("bootstrap.servers", "192.168.222.80:9092,192.168.222.81:9092,192.168.222.82:9092");
//        properties.put("bootstrap.servers", "192.168.223.100:9092,192.168.223.101:9092,192.168.223.102:9092");
        properties.put("group.id", "aaaa");
        // 启动偏移量自动提交服务
        properties.put("enable.auto.commit", "true");
        // 设置偏移量自动提交时间间隔10秒钟
        properties.put("auto.commit.interval.ms", "10000");
        // 设置偏移量消费设置 最早
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return properties;
    }



}
