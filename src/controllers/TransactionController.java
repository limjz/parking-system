package controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import utils.Config;
import utils.FileHandler;

public class TransactionController {

  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public void logTransaction (String plate, double amountPaid, String paymentMehod, String note){
      String record = String.join(Config.DELIMITER_WRITE, 
              LocalDateTime.now().format(FMT),
              plate,
              String.format("%.2f", amountPaid),
              paymentMehod, 
              note
          );
      FileHandler.appendData(Config.TRANSACTION_FILE, record);
  }

  public double getTotalRevenue (){ 
      List<String> lines = FileHandler.readAllLines(Config.TRANSACTION_FILE);
      double total = 0.0;
      for (String line : lines){
          try {
                String[] parts = line.split(Config.DELIMITER_READ);
                if (parts.length >= 3) {
                    // Column 2 is the Amount
                    total += Double.parseDouble(parts[2]);
                }
            } catch (Exception e) {
                // Skip bad lines
            }
      }

      return total;
  }





}
