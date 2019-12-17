package com.test;

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
import java.util.Random;

public class CutterTest1 {
    private static String[] events = {"下单","支付","退货"};
    private static String[] userName = {"小张","小李","小刘","小刘","小赵","小吴","小季"};
    private static int[] eventStatus = {200,404,500};
    private static int[] userId = {1,2,3,4,5,6,7};
    private static char[] chars = {'|','|'};
    public static void main(String[] args) throws IOException {

        // 单分隔符 数据
        String seq = "|";
        String path = ".\\src\\main\\resources\\cutter2.txt";
        // 多分隔符 数据
        seq = "&*";
        // 分隔符结合使用 数据
        seq = "|";

        long timetamp =  0;
        Path logFile = Paths.get(path);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            timetamp = sdf.parse("2018-08-17 10:33:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            for (int i = 0; i < 100; i++) {
                StringBuilder stringBuilder = new StringBuilder();
                timetamp = timetamp + 10000;
                stringBuilder.append(sdf.format(new Date(timetamp))).append(seq);
                String event = events[getRandom(3)];
                stringBuilder.append(event);
                if (event.equals("支付")){
                    stringBuilder.append(seq).append(eventStatus[getRandom(3)]);
                }else if (event.equals("下单")){
                    stringBuilder.append(seq).append(userName[getRandom(7)]).append(chars[getRandom(2)]).append(userId[getRandom(7)]);
                }

                writer.newLine();
                writer.write(stringBuilder.toString());
            }
        }

    }

    private static int getRandom(int size)
    {
        Random random = new Random();
        return random.nextInt(size);
    }
}
