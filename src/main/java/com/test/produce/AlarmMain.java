package com.test.produce;

import com.test.tool.FileReader;
import com.test.tool.ProduceFactory;
import com.test.tool.Producor;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Iterator;

public class AlarmMain {
    public static void main(String[] args) {
//       testMethod2();
       testMethod3();
    }

    /**
     * web-proxy-nginx-log
     * 997896
     *
     */
    public static void testMethod3() {

        String topic = "123abc";
        String path = "alarmJson.txt";
        Producor.sendMassageIntoKafKa(topic,path,1000);
    }

    public static void testMethod2() {
        String topic = "ccccccc";
        String path = "alarmJson.txt";
        Producor.sendMassageIntoKafKa(topic,path,1000);
    }

    public static void testMethod1() {
        String topic = "666383";
        String path = "alarmJson.txt";
        Producor.sendMassageIntoKafKa(topic,path,1000);
    }
}
