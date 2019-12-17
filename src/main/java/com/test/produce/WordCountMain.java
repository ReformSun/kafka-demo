package com.test.produce;

import com.test.tool.Producor;

public class WordCountMain {
    public static void main(String[] args) {
        String topic = "wordcount";
        String path = "wordCount.txt";
        Producor.sendMassageIntoKafKa(topic,path,1000);
    }
}
