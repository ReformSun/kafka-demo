package com.test.produce;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.test.tool.ProduceFactory;
import com.test.tool.Producor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TestMain2 {
    private static String path = "test2.txt";
    public static void main(String[] args) {
        testMethod1();
    }

    public static void testMethod1() {
        String topic = "ddddddd";
        Producor.sendMassageIntoKafKa(topic,path,1000);
    }


}
