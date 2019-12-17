package com.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Test4 {
    private static String[] character = {"A"};
    private static Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    public static void main(String[] args) throws IOException {
        Path logFile = Paths.get(".\\src\\main\\resources\\testJson.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            long time =  1534472180000L;
            for (int i = 0; i < 100; i++) {
                Map<String,Object> map = new HashMap<>();
                map.put("k",character[0]);
                time = time + (getRandom(5) +1) * 1000;
                map.put("rtime",time);
                String s = gson.toJson(map);
                writer.newLine();
                writer.write(s);
            }

        }
    }


    private static int getRandom(int size)
    {
        Random random = new Random();
        return random.nextInt(size);
    }


    public static void testMethod1() {
        List<Object> list = new ArrayList<>();
        testMethod(list);
        list.add("ss");

    }

    public static void testMethod(List<Object> o) {

    }
}
