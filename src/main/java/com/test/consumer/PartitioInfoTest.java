package com.test.consumer;

import com.test.partitioner.DefaultPartitionerTest;
import com.test.tool.Consumer;
import com.test.tool.ConsumerFactory;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.PartitionInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartitioInfoTest {
    public static void main(String[] args) {
//        testMethod1();
//        testMethod2();
        testMethod3();
    }

    /**
     * 得到全部的主题名
     */
    public static void testMethod1(){
        KafkaConsumer<String, String> kafkaConsumer = ConsumerFactory.getKafkaConsumer("datasetips_1");
        List<String> list = new ArrayList<>(kafkaConsumer.listTopics().keySet());
        for (String s:list) {
            System.out.println(s);
        }
    }

    /**
     * 获取指定的主题分区信息
     */
    public static void testMethod2(){
        KafkaConsumer<String, String> kafkaConsumer = ConsumerFactory.getKafkaConsumer("datasetips_1");
        List<PartitionInfo> list = kafkaConsumer.partitionsFor("separator_ips_out_topic");
        for (PartitionInfo partitionInfo:list){
            System.out.println(partitionInfo.partition());
        }
    }

    /**
     * 分区
     */
    public static void testMethod3(){
        KafkaConsumer<String, String> kafkaConsumer = ConsumerFactory.getKafkaConsumer("dataset_ips");
        DefaultPartitionerTest defaultPartitioner = new DefaultPartitionerTest();
        int i = defaultPartitioner.partition("log_huawei","key","192.168.205.65".getBytes(),"","".getBytes(),kafkaConsumer);
        System.out.println(i);

    }
}
