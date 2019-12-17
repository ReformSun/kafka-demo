package com.test.produce;

import com.test.tool.ProduceFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Ips {
    private static final String defaultDateFormat = "yyyy/MM/dd HH:mm:ss";

    public static void main(String[] args) {
        String topic = "ips";
        sendMassageIntoApmCutterKafKa(topic,10000);
    }

    public static void sendMassageIntoApmCutterKafKa(String topic,long inv){
        Producer<String, String> producer = null;
        try {
            producer = ProduceFactory.getKafkaProducer();
            long time = System.currentTimeMillis();
            while (true){
                StringBuilder waf = new StringBuilder("July 23 20:36:14 July 23 2019 20:36:30+08:00 XXY_I_EXT_FW_1000E_N16_1 IPSTRAP/4/THREATTRAP:OID 1.3.6.1.4.1.2011.6.122.43.1.2.8 An intrusion was detected.( SrcIp=122.114.156.19, DstIp=10.85.1.2, SrcPort=54001, DstPort=80,  Protocol=TCP, Event=Web Backdoor - PHP Tiny Webshell in HTTP URL Parameter, DetectTime=");
                waf.append(toDate(time));
                waf.append("),数据来源/192.168.5.16");
                producer.send(new ProducerRecord<String, String>(topic,waf.toString()));
                time = time + 30000;
                Thread.sleep(inv);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (producer != null){
                producer.close();
            }
        }
    }

    public static String toDate(long time){
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultDateFormat);
        return simpleDateFormat.format(date);
    }
}
