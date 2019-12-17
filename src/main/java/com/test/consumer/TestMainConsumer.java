package com.test.consumer;

import com.test.tool.Consumer;
import com.test.tool.ConsumerFactory;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestMainConsumer {
    private static Path logFile = Paths.get("./src/main/resources/consumer2.txt");
    public static void main(String[] args) {
//        testMethod1();
        testMethod2();
    }

    public static void testMethod1(){
        KafkaConsumer<String, String> kafkaConsumer = ConsumerFactory.getKafkaConsumer();
        Map map  = kafkaConsumer.listTopics();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();

            System.out.println(entry.getKey());


        }

    }

    public static void testMethod2(){
        // 获取每个partition一个小时之前的偏移量
        KafkaConsumer<String, String> consumer = ConsumerFactory.getKafkaConsumer();
        String topic = "abcdef";
        try {
            // 获取topic的partition信息
            List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
            List<TopicPartition> topicPartitions = new ArrayList<>();

            Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            long nowTime = now.getTime();
            System.out.println("当前时间: " + df.format(now));
            long fetchDataTime = nowTime - 1000 * 60 * 6;  // 计算2个小时之前的时间戳

            for(PartitionInfo partitionInfo : partitionInfos) {
                topicPartitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
                timestampsToSearch.put(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()), fetchDataTime);
            }

            consumer.assign(topicPartitions);

            // 获取每个partition一个小时之前的偏移量
            Map<TopicPartition, OffsetAndTimestamp> map = consumer.offsetsForTimes(timestampsToSearch);

            OffsetAndTimestamp offsetTimestamp = null;
            System.out.println("开始设置各分区初始偏移量...");
            for(Map.Entry<TopicPartition, OffsetAndTimestamp> entry : map.entrySet()) {
                // 如果设置的查询偏移量的时间点大于最大的索引记录时间，那么value就为空
                offsetTimestamp = entry.getValue();
                if(offsetTimestamp != null) {
                    int partition = entry.getKey().partition();
                    long timestamp = offsetTimestamp.timestamp();
                    long offset = offsetTimestamp.offset();
                    System.out.println("partition = " + partition +
                            ", time = " + df.format(new Date(timestamp))+
                            ", offset = " + offset);
                    // 设置读取消息的偏移量
                    consumer.seek(entry.getKey(), offset);
                }
            }
            System.out.println("设置各分区初始偏移量结束...");
            Consumer.receiveMassage(consumer,logFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }

    }

}
