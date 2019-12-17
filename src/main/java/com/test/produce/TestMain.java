package com.test.produce;

import com.test.tool.FileReader;
import com.test.tool.ProduceFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;


/**
 * 测试切分器的      数据格式   时间|类目|省份|市|区|性别|身高|数量|总价
 */
public class TestMain {
    public static void main(String[] args) {

        Producer<String, String> producer = null;
        try {
            producer = ProduceFactory.getKafkaProducer();
            String topic = "monitorBlocklyQueueKey1";
            // 测试1
            String path = ".\\src\\main\\resources\\test1.txt";
            // 测试2
            path = ".\\src\\main\\resources\\test2.txt";
            // 测试3
            path = ".\\src\\main\\resources\\test3.txt";
            // 测试4
            path = ".\\src\\main\\resources\\test4.txt";
            // 测试5
            path = ".\\src\\main\\resources\\test5.txt";
            // 测试6
            path = ".\\src\\main\\resources\\testJson.txt";
            topic = "monitorBlocklyQueueKeyJson2";




            Iterator<String> iterator = null;
            try {
                iterator = FileReader.readFile(path).iterator();
                while (iterator.hasNext()){
                    producer.send(new ProducerRecord<String, String>(topic,iterator.next()));
                   Thread.sleep(1000);
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
