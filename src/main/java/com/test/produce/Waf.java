package com.test.produce;

import com.test.tool.FileReader;
import com.test.tool.ProduceFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class Waf {
    private static final String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) {
        String topic = "SDNGateway3";
        sendMassageIntoApmCutterKafKa(topic,10000);
    }

    public static void sendMassageIntoApmCutterKafKa(String topic,long inv){
        Producer<String, String> producer = null;
        try {
            producer = ProduceFactory.getKafkaProducer();
            long time = System.currentTimeMillis();
            while (true){
                StringBuilder waf = new StringBuilder("Jan 14 14:06:45 localhost DBAppWAF: 发生时间/");
                waf.append(toDate(time));
                waf.append(",威胁/低,事件/爬虫,请求方法/POST,URL地址/www.shanghaimuseum.net/museum/frontend/en/fulltextsearch/full-text-search.action;jsessionid=91CB98AB3B74A78336C2E7518AF5D23F,POST数据/fullSearchText=8\\\\\\\\n,服务器IP/101.227.180.64,主机名/www.shanghaimuseum.net,服务器端口/80,客户端IP/210.52.224.23,客户端端口/54497,客户端环境/Mozilla/5.0 (Windows NT 6.2; rv:30.0) Gecko/20150101 Firefox/32.0 360Spider,标签/爬虫,动作/告警,HTTP/S响应码/302,攻击特征串/360spider,触发规则/17020002,访问唯一编号/AYqcA0AtAcAcAcArAcAPAcAc,国家/中国,省/上海,市/NIL");
                waf.append(",数据来源/10.4.251.99");
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
