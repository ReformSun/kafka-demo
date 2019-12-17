package com.test.tool;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Consumer {
    public static void receiveMassage(KafkaConsumer<String,String> kafkaConsumer,Path logFile){
        int count = 0;
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                try {
                    try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
                        writer.newLine();
                        if (record.value() != null){
                            count ++;
//                            writer.write(record.value() + " topic:" + record.topic());
                            writer.write(record.value());
//                            writer.write(count + "");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
