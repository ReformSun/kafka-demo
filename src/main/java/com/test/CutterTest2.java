package com.test;


import com.test.tool.RandomUtil;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CutterTest2 {
    private static String[] userName = {"小张","小李","小刘","小刘","小赵","小吴","小季"};
    private static String[] sexs = {"男","女"};
    private static String seq = "|";
    public static void main(String[] args) {
        String path = ".\\src\\main\\resources\\cutter2.txt";
        Path logFile = Paths.get(path);
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            for (int i = 0; i < 100; i++) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("{\"message\":\"");
                stringBuilder.append(userName[RandomUtil.getRandom(7)]).append(seq);
                stringBuilder.append(sexs[RandomUtil.getRandom(2)]).append(seq);
                stringBuilder.append(RandomUtil.getRandom(30));
                stringBuilder.append("\"}");
                writer.newLine();
                writer.write(stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
