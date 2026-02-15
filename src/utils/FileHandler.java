package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler { 

  public static List<String> readAllLines(String filename) {
      List<String> lines = new ArrayList<>();
      File file = new File(filename);

      if (!file.exists()) {
        // Auto-create directory and file if missing
        try {
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) { e.printStackTrace(); }
        return lines;
      }

      try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
          if (!line.trim().isEmpty()) lines.add(line);
        }
      } catch (IOException e) {
        System.err.println("Error reading file: " + e.getMessage());
      }
      return lines;
  }
   
  public static void appendData(String filepath, String data) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, true))) { 
        writer.write(data);
        writer.newLine();
      } catch (IOException e) {
        System.err.println("Error writing to file: " + e.getMessage());
      }
  }

  public static void updateData(String filepath, String data) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false))) { 
        writer.write(data);
      } catch (IOException e) {
        System.err.println("Error updating file: " + e.getMessage());
      }
  }
}