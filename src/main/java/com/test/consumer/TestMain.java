package com.test.consumer;

import com.test.tool.Consumer;
import com.test.tool.ConsumerFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.locks.LockSupport;

public class TestMain {
    private static Path logFile = Paths.get("./src/main/resources/consumer.txt");
    public static void main(String[] args) throws IOException {


        testMethod1();
//        testMethod2();
//        testMethod3();
    }


    /**
     * web-proxy-nginx-log
     * 997896
     * log_huawei
     * 133174
     * 963672
     * SDNGateway3
     * separator_waf_out_topic
     */
    public static void testMethod1(){
        KafkaConsumer<String, String> kafkaConsumer = ConsumerFactory.getKafkaConsumer("dataset_ips_test");
        kafkaConsumer.subscribe(Arrays.asList("log_huawei"));
        Consumer.receiveMassage(kafkaConsumer,logFile);
    }
    /**
     * 从开始位置消费
     * analyticEventDataQueueKey
     * customEventQueueKey
     * errorDataQueueKey
     * errorEventDataQueueKey
     *
     * rawTransactionBreakDownMetricQueueKey
     * rawWebTransactionMetricQueueKey
     * rawBackGroundTransactionMetricQueueKey
     * rawDatabaseTransactionMetricQueueKey
     * rawJvmClassCountMetricQueueKey
     * rawJvmCodeCacheMetricQueueKey
     * rawJvmGcCpuTimeMetricQueueKey
     * rawJvmHttpSessionMetricQueueKey
     * rawJvmPsEdenSpaceMetricQueueKey
     * rawJvmPsOldGenMetricQueueKey
     * rawJvmHeapMemoryUsageMetricQueueKey
     * rawJvmPsPermGenMetricQueueKey
     * rawJvmPsSurvivorSpaceMetricQueueKey
     * rawJvmThreadCountMetricQueueKey
     * rawJvmThreadPoolMetricQueueKey
     * rawServerCpuRateMetricQueueKey
     * rawServerMemoryUsageMetricQueueKey
     * rawServerRespTimeMetricQueueKey
     * rawServerApdexMetricQueueKey
     * rawServerDiskSpaceMetricQueueKey
     *
     *
     */
    public static void testMethod2(){
        List<String> list = new ArrayList(){{
            add("rawTransactionBreakDownMetricQueueKey");
            add("rawWebTransactionMetricQueueKey");
            add("rawBackGroundTransactionMetricQueueKey");
            add("rawDatabaseTransactionMetricQueueKey");
            add("rawJvmClassCountMetricQueueKey");
            add("rawJvmCodeCacheMetricQueueKey");
            add("rawJvmGcCpuTimeMetricQueueKey");
            add("rawJvmHttpSessionMetricQueueKey");
            add("rawJvmPsEdenSpaceMetricQueueKey");
            add("rawJvmPsOldGenMetricQueueKey");
            add("rawJvmHeapMemoryUsageMetricQueueKey");
            add("rawJvmPsPermGenMetricQueueKey");
            add("rawJvmPsSurvivorSpaceMetricQueueKey");
            add("rawJvmThreadCountMetricQueueKey");
            add("rawJvmThreadPoolMetricQueueKey");
            add("rawServerCpuRateMetricQueueKey");
            add("rawServerMemoryUsageMetricQueueKey");
            add("rawServerRespTimeMetricQueueKey");
            add("rawServerApdexMetricQueueKey");
            add("rawServerDiskSpaceMetricQueueKey");
        }};
        KafkaConsumer<String, String> kafkaConsumer = ConsumerFactory.getKafkaConsumer("abcd");
        kafkaConsumer.subscribe(list);
        Consumer.receiveMassage(kafkaConsumer,logFile);
    }

    /**
     * 从指定位置消费
     */
    public static void testMethod3(){
        KafkaConsumer<String, String> kafkaConsumer = ConsumerFactory.getKafkaConsumer();
        TopicPartition tp = new TopicPartition("web-proxy-nginx-log", 0);
        kafkaConsumer.assign(Collections.singleton(tp));

        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        // 设置 offset 为 0
        offsets.put(tp, new OffsetAndMetadata(0, "reset"));
        kafkaConsumer.commitSync(offsets);
//        kafkaConsumer.subscribe(Arrays.asList("abcdef"));
        Consumer.receiveMassage(kafkaConsumer,logFile);
    }
}
