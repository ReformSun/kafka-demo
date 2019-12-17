package com.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class Test {
    private static String[] categorys = {"男装","女装","食品"};
    private static String[] province = {"河南","浙江","陕西"};
    private static String[] city = {"郑州","杭州","西安"};
    private static String[] area = {"二七区","西湖区","灞桥区"};
    private static String[] sex = {"男","女"};
    public static void main(String[] args) throws IOException {

        Path logFile = Paths.get(".\\src\\main\\resources\\test1.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            for (int i = 0; i < 1000; i++) {
                StringBuilder stringBuilder = new StringBuilder();
                long timetamp = System.currentTimeMillis();
                stringBuilder.append(timetamp).append("|");
                stringBuilder.append(categorys[getRandomTable()]).append("|");
                stringBuilder.append(province[getRandomTable()]).append("|");
                stringBuilder.append(city[getRandomTable()]).append("|");
                stringBuilder.append(area[getRandomTable()]).append("|");
                stringBuilder.append(sex[getRandomSex()]).append("|");
                stringBuilder.append(getheight()).append("|");
                stringBuilder.append(getcount()).append("|");
                stringBuilder.append(getprice()).append("|");
                writer.newLine();
                writer.write(stringBuilder.toString());
            }
        }

    }


    private static int getRandomTable()
    {
        Random random = new Random();
        return random.nextInt(3);
    }

    private static int getRandomSex()
    {
        Random random = new Random();
        return random.nextInt(2);
    }
    private static int getheight(){
        Random random = new Random();
        return random.nextInt(200) + 100;
    }

    private static int getcount(){
        Random random = new Random();
        return random.nextInt(10) + 1;
    }

    private static int getprice(){
        Random random = new Random();
        return random.nextInt(1000) + 1;
    }
}
