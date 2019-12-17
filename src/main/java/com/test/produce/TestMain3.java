package com.test.produce;

import com.test.tool.ProduceFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;


public class TestMain3 {
    public static void main(String[] args) {
        Producer<String, String> producer = null;
        try {
            producer = ProduceFactory.getKafkaProducer();
            String topic = "monitorBlocklyQueueKey1";
//            正常测试
            String x = "33,ss:ee|xx";
            x = "33,ss:ee|";
            x = "33,ss:ee";
//            单分隔符
//            x = "|33|ss|ee|";
//
////            多分隔符
//            x = "多分隔符*ssdee|";

            x = "ss|xx|2|DD|SS|9";

//            json 切分器

            //            kv 切分器
            x = "keyname1=1;keyname2=d;keyname3=e;keyname4=e;keyname5=d";


            for (int i = 35; i < 40; i++) {
                long s = 1534472180000L + i * 10000;
                x = "{\"d\":5,\"b\":20,\"rtime\":" + s + ",\"a\":\"小赵\"}";
                topic = "monitorBlocklyQueueKeyJson2";
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
