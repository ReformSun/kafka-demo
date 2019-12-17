package com.test.produce;

import com.test.tool.ProduceFactory;
import com.test.tool.RandomUtil;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class TestMainProducer {
    public static void main(String[] args) {
        Producer<String, String> producer = null;
        try {
            producer = ProduceFactory.getKafkaProducer();
            String[] strings = {"a","b","c"};
            String topic = "ddddddd";
//            正常测试

            for (int i = 0; i < 40; i++) {
                String x = strings[RandomUtil.getRandom(3)];
                producer.send(new ProducerRecord<String, String>(topic,x));
                Thread.sleep(1000);
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
