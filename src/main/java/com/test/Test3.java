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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Test3 {
    private static String[] userName = {"小张","小李","小刘","小刘","小赵","小吴","小季"};
    private static Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    public static void main(String[] args) throws IOException {
        Path logFile = Paths.get(".\\src\\main\\resources\\testJson.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            long time =  1534472180000L;
//            time = 1534472192000L;
            for (int i = 0; i < 20; i++) {
                Map<String,Object> map = new HashMap<>();
                map.put("a",userName[getRandom(7)]);
                map.put("b",getRandom(3));
                time = time + (getRandom(5) +1) * 1000;
                map.put("rtime",time);
                String s = gson.toJson(map);
                writer.newLine();
                writer.write(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static int getRandom(int size)
    {
        Random random = new Random();
        return random.nextInt(size);
    }
}
