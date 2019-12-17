package com.test;

import com.test.tool.RandomUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WordCountTest1 {
    private static String[] citys = {"井冈山","上海","杭州","郑州","徐州","黑龙江","重庆","成都"};
    private static String seq = "|";
    public static void main(String[] args) {
        String path = ".\\src\\main\\resources\\wordCount.txt";
        Path logFile = Paths.get(path);

        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            for (int i = 0; i < 100; i++) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(citys[RandomUtil.getRandom(6)]).append(seq);
                stringBuilder.append(citys[RandomUtil.getRandom(6)]).append(seq);
                stringBuilder.append(citys[RandomUtil.getRandom(6)]).append(seq);
                stringBuilder.append(citys[RandomUtil.getRandom(6)]).append(seq);
                writer.newLine();
                writer.write(stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
