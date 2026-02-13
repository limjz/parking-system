package controllers;

import java.util.ArrayList;
import java.util.List;
import models.Debt;
import utils.Config;
import utils.FileHandler;

public class DebtController {

  
  public List<Debt> getAllDebts() { 

    List<String> lines = FileHandler.readAllLines(Config.DEBT_FILE);
    List<Debt> debts = new ArrayList<>(); 
    
    for(String line : lines){ 
      if (!line.trim().isEmpty()){ 
        debts.add(new Debt(line));
      }
    }

    return debts;
  }
  
  private void saveAllDebts(List<Debt> debts) {
        StringBuilder sb = new StringBuilder();
        for (Debt d : debts) {
            sb.append(d.toFileString()).append("\n");
        }
        FileHandler.updateData(Config.DEBT_FILE, sb.toString().trim());
    }

  // Add new debt (or update existing)
  public void addDebt(String plate, double amount) {
        List<Debt> allDebts = getAllDebts();
        boolean found = false;

        // Check if this car already has a debt record
        for (Debt d : allDebts) {
            if (d.getPlate().equalsIgnoreCase(plate)) {
                d.addAmount(amount); // Update existing object
                found = true;
                break;
            }
        }

        // If not found, create a new Debt object
        if (!found) {
            allDebts.add(new Debt(plate, amount));
        }

        saveAllDebts(allDebts);
    }


  public double getDebtAmount (String plateNumber){ 
    List<Debt> allDebts = getAllDebts();

    for (Debt d : allDebts){ 
      if (d.getPlate().equals(plateNumber)){ 
        return d.getDebtAmount();
      }
    }

    return 0.0;
  }

  // Clear debt (after payment)
  public void clearDebt(String plate) {
        List<Debt> allDebts = getAllDebts();
        // Remove the debt object from the list if plate matches
        allDebts.removeIf(d -> d.getPlate().equalsIgnoreCase(plate));
        saveAllDebts(allDebts);
    }

  



}
