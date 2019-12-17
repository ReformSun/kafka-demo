package com.test.tool;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.IOException;
        import java.nio.charset.StandardCharsets;
        import java.nio.file.*;
        import java.util.ArrayList;
        import java.util.List;

public class FileWriter {
    public static void main(String[] args) {
        try {

            writerFile("cffhh");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writerFile(String s) throws IOException {
        Path logFile = Paths.get("./src/main/resources/test1.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8, StandardOpenOption.APPEND)){
            writer.newLine();
            writer.write(s);
        }

    }
}
