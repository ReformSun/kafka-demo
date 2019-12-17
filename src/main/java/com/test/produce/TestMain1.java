package com.test.produce;

import com.test.tool.Producor;

public class TestMain1 {
    private static  String path = "test1.txt";
    public static void main(String[] args) {
        testMethod1();
    }

    public static void testMethod1() {
        String topic = "ccccccc";
        Producor.sendMassageIntoKafKa(topic,path,1000);
    }

}
