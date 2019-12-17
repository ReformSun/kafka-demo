package com.test.produce;

import com.test.tool.Producor;

public class DataSetMain {
    public static void main(String[] args) {
        String topic = "793214";
        String path = "dataset.txt";
        Producor.sendMassageIntoKafKa(topic,path,1000);
    }
}
