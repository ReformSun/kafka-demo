package com.test.produce;

import com.test.tool.Producor;

public class CutterMain {
    private static String path = "cutter2.txt";
    public static void main(String[] args) {
//        testMethod2();
//        testMethod3();
        testMethod4();
    }

    public static void testMethod1() {
        String topic = "dddd";
        Producor.sendMassageIntoKafKa(topic,path,10000);
    }

    public static void testMethod2() {
        String topic = "cccccccc";
        Producor.sendMassageIntoApmCutterKafKa(topic,path,1000);
    }

    /**
     * test9-19
     */
    public static void testMethod3() {
        String topic = "ddddddd";
        Producor.sendMassageIntoApmCutterKafKa(topic,path,1000);
    }

    /**
     * test9-27
     */
    public static void testMethod4() {
        String topic = "log_testtttt";
        Producor.sendMassageIntoApmCutterKafKa(topic,path,1000);
    }
}
