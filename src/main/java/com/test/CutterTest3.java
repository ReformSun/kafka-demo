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

public class CutterTest3 {
    private static String[] kecengs = {"中男","老男","老女","青男","青女","中女"};
    private static String seq = "|";
    public static void main(String[] args) {
        String path = ".\\src\\main\\resources\\cutter2.txt";
        Path logFile = Paths.get(path);

        long timetamp =  0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            timetamp = sdf.parse("2018-09-20 1:33:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        timetamp = 1537379380000L;
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            for (int i = 0; i < 100000; i++) {
                timetamp = timetamp + 10000;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(sdf.format(new Date(timetamp))).append(seq);
                stringBuilder.append(RandomUtil.getRandom(1000) + 1).append(seq);
                stringBuilder.append(kecengs[RandomUtil.getRandom(6)]);
                writer.newLine();
                writer.write(stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
