package com.test.tool;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Producor {
    public static void sendMassageIntoKafKa(String topic,String contentPath,long inv){
        Producer<String, String> producer = null;
        try {
            producer = ProduceFactory.getKafkaProducer();
            Iterator<String> iterator = null;
//            String path = ".\\src\\main\\resources\\" + contentPath;
            String path = "./src/main/resources/" + contentPath;
            try {
                iterator = FileReader.readFile(path).iterator();
                while (iterator.hasNext()){
                    String massage = iterator.next();
                    System.out.println(massage);
                    Header header = new Header() {
                        @Override
                        public String key() {
                            return "ddd";
                        }

                        @Override
                        public byte[] value() {
                            return "ddd".getBytes();
                        }
                    };
                    List<Header> list = new ArrayList<>();
                    list.add(header);
                    producer.send(new ProducerRecord<String, String>(topic,0,"bb",massage,list));
                    Thread.sleep(inv);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (producer != null){
                producer.close();
            }
        }
    }

    public static void sendMassageIntoApmCutterKafKa(String topic,String contentPath,long inv){
        Producer<String, String> producer = null;
        try {
            producer = ProduceFactory.getKafkaProducer();
            Iterator<String> iterator = null;
            String path = ".\\src\\main\\resources\\" + contentPath;
            try {
                iterator = FileReader.readFile(path).iterator();
                while (iterator.hasNext()){
                    String massage = iterator.next();
                    StringBuilder stringBuilder = new StringBuilder("{\"message\":\"");
                    System.out.println(massage);
                    stringBuilder.append(massage);
                    stringBuilder.append("\"}");
                    producer.send(new ProducerRecord<String, String>(topic,stringBuilder.toString()));
                    Thread.sleep(inv);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (producer != null){
                producer.close();
            }
        }
    }
}
