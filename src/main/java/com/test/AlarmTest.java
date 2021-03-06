package com.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.test.tool.URLUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AlarmTest {

    private static Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    public static void main(String[] args) throws IOException {
        long time =  0L;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            time = sdf.parse("2018-09-20 1:33:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        testMethod2();
//        testMethod1(time);
//        testMethod3(1537390812000L + 6000L);
        testMethod4();
//        testMethod5(time);
    }

    public static void testMethod2(long time) {
        String[] provinces = {"河南","浙江","陕西","辽宁","安徽","湖南"};
        String[] citys = {"井冈山","上海","杭州","郑州","徐州","黑龙江"};
        Path logFile = Paths.get(".\\src\\main\\resources\\alarmJson.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            for (int i = 0; i < 100; i++) {
                Map<String,Object> map = new HashMap<>();
                map.put("SUM_sales_index",getRandom(2000));
                time = time + 60000;
                map.put("datatime",time);
                map.put("city",citys[getRandom(6)]);
                map.put("province",provinces[getRandom(6)]);
                String s = gson.toJson(map);
                writer.newLine();
                writer.write(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testMethod4() {
        Path logFile = Paths.get(URLUtil.baseUrl+"alarmJson.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            for (int i = 0; i < 10000; i++) {
                String a = i + "";
                writer.newLine();
                writer.write(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testMethod3(long time) {
        String[] provinces = {"河南","浙江","陕西","辽宁","安徽","湖南"};
        String[] citys = {"井冈山","上海","杭州","郑州","徐州","黑龙江"};
        String[] sexs = {"男","女"};
        Path logFile = Paths.get(".\\src\\main\\resources\\alarmJson.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            for (int i = 0; i < 100; i++) {
                Map<String,Object> map = new HashMap<>();
                if (i < 10){
                    map.put("SUM_sales_index",100);
                }else if (i < 20){
                    map.put("SUM_sales_index",50);
                }else if (i < 30){
                    map.put("SUM_sales_index",100);
                }else{
                    map.put("SUM_sales_index",50);
                }
//                map.put("SUM_sales_index",getRandomMaxAndMin(1000,10));
                map.put("MAX_sales_index",getRandom(2000));
                map.put("datatime",time);
                time = time + 60000;
                map.put("city",citys[getRandom(6)]);
                map.put("province",provinces[getRandom(6)]);
                map.put("sex",sexs[getRandom(2)]);
                String s = gson.toJson(map);
                writer.newLine();
                writer.write(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testMethod1(long time) {
        String[] userName = {"小张","小李","小刘","小刘","小赵","小吴","小季"};
//        Path logFile = Paths.get(".\\src\\main\\resources\\alarmJson.txt");
        Path logFile = Paths.get("./src/main/resources/alarmJson.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            for (int i = 0; i < 10; i++) {
                Map<String,Object> map = new HashMap<>();
                map.put("user_name",userName[getRandom(1)]);
                if (i < 10){
                    map.put("user_count",100);
                }else if (i < 20){
                    map.put("user_count",50);
                }else if (i < 30){
                    map.put("user_count",100);
                }else{
                    map.put("user_count",50);
                }
//                map.put("user_count",getRandom(4) + 1);
                map.put("_sysTime",time);
//                map.put("发生时间",time);
                time = time + 60000;
                String s = gson.toJson(map);
                writer.newLine();
                writer.write(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testMethod5(long time) {
        Path logFile = Paths.get(".\\src\\main\\resources\\test1.txt");
        logFile = Paths.get(".\\src\\main\\resources\\test2.txt");
        long number = 1;
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            for (int i = 0; i < 100000; i++) {
                Map<String,Object> map = new HashMap<>();
                map.put("timestamp",time);
                time = time + 1000;
                map.put("number",number);
                number++;
                String s = gson.toJson(map);
                writer.newLine();
                writer.write(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testMethod6(){

    }


    private static int getRandom(int size)
    {
        Random random = new Random();
        return random.nextInt(size);
    }

    private static int getRandomMaxAndMin(int max,int min)
    {
        Random random = new Random();
        return random.nextInt(max) + min;
    }
}
