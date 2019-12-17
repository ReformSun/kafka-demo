package com.test.consumer;

import com.test.tool.Consumer;
import com.test.tool.ConsumerFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

public class TestMain2 {

    private static Path logFile = Paths.get("./src/main/resources/consumer2.txt");
    public static void main(String[] args) {
//        testMethod1();
//        testMethod2();
        testMethod3();
    }


    /**
     * 同一个topic 同一个groupId 和TestMain testMethod1方法测试
     */
    public static void testMethod1(){
        KafkaConsumer<String, String> kafkaConsumer = ConsumerFactory.getKafkaConsumer();
        kafkaConsumer.subscribe(Arrays.asList("abcdef"));
        Consumer.receiveMassage(kafkaConsumer,logFile);
    }
    /**
     * 同一个topic 不同groupId 和TestMain testMethod2方法测试
     */
    public static void testMethod2(){
        KafkaConsumer<String, String> kafkaConsumer = ConsumerFactory.getKafkaConsumer("adcde");
        kafkaConsumer.subscribe(Arrays.asList("abcdef"));
        Consumer.receiveMassage(kafkaConsumer,logFile);
    }

    /**
     * 指定分组
     * 得到指定topic 指定分区的信息
     */
    public static void testMethod3(){
        KafkaConsumer<String, String> kafkaConsumer = ConsumerFactory.getKafkaConsumer("1");
        TopicPartition topicPartition = new TopicPartition("log_huawei",8);
        kafkaConsumer.assign(Arrays.asList(topicPartition));
        kafkaConsumer.seek(topicPartition,162346826);
        Consumer.receiveMassage(kafkaConsumer,logFile);
    }
}
