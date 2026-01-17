package utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SimpleFile {

    public static List<String> readAllLines(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) return new ArrayList<>();
            return Files.readAllLines(Paths.get(path));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void writeText(String path, String text, boolean append) {
        try {
            File f = new File(path);
            File parent = f.getParentFile();
            if (parent != null) parent.mkdirs();

            try (FileWriter fw = new FileWriter(f, append);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(text);
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Write failed: " + e.getMessage());
        }
    }
}
