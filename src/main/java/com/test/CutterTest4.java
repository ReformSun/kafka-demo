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

public class CutterTest4 {
    private static String[] provinces = {"河南","浙江","陕西","辽宁","安徽","湖南"};
    private static String[] citys = {"井冈山","上海","杭州","郑州","徐州","黑龙江"};
    private static String[] sexs = {"男","女"};
    private static String seq = "|";
    public static void main(String[] args) {
        String path = ".\\src\\main\\resources\\cutter2.txt";
        Path logFile = Paths.get(path);

        long timetamp =  0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            timetamp = sdf.parse("2018-09-26 1:33:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        timetamp = 1537379380000L;
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            for (int i = 0; i < 10000; i++) {
                timetamp = timetamp + 100000;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(provinces[RandomUtil.getRandom(6)]).append(seq);
                stringBuilder.append(citys[RandomUtil.getRandom(6)]).append(seq);
                stringBuilder.append(sexs[RandomUtil.getRandom(2)]).append(seq);
                stringBuilder.append(RandomUtil.getRandom(3000) + 1).append(seq);
                stringBuilder.append(sdf.format(new Date(timetamp)));
                writer.newLine();
                writer.write(stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
