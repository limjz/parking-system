package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler{ 

   public static List<String> readAllLines (String filename)
  {
    List<String> lines = new ArrayList<>();
    File file = new File(filename);

    if (!file.exists()) {
      System.out.println("File not found: " + filename);
      return lines;
    }

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      // Read each line and add to the list
      while ((line = br.readLine()) != null) {
        lines.add(line);
      }
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    }

    return lines;

  }
   
  // Append data to a file (add a new line)
  public static void appendData (String filepath, String data)
  {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, true))) { // append = true meaning add a new line
      writer.write(data);
      writer.newLine();

    } catch (IOException e) {
      System.err.println("Error writing to file: " + e.getMessage());
    }
  }

  // Update (overwrite) data in a file
  public static void updateData (String filepath, String data)
  {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false))) { //append = false meaning rewrite the file
      writer.write(data);
      writer.newLine();

    } catch (IOException e) {
      System.err.println("Error updating file: " + e.getMessage());
    }

  }
}