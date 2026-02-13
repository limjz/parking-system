package models;


import utils.Config;


public class Debt {

  private String plateNumber; 
  private double debtAmount; 


  // contructor 1
  public Debt (String plate, double debt){ 
    this.plateNumber = plate; 
    this.debtAmount = debt;
  }

  // contructor 2 // get data from file 
  public Debt (String fileLine){ 
    try {
        
        String[] parts = fileLine.split(Config.DELIMITER_READ);
        this.plateNumber = parts [0]; 
        this.debtAmount = Double.parseDouble(parts [1]); // convert string to double

    } catch (Exception e) {
      this.plateNumber = "UNKNOWN"; 
      this.debtAmount = 0.0; 
    }
  }

  public String toFileString (){ 
    return plateNumber + Config.DELIMITER_WRITE + String.format("%.2f", debtAmount);
  }

  public void addAmount (double amount){ 
    this.debtAmount += amount; 
  }
  
  public void clear () { 
    this.debtAmount = 0.0;  
  }

  // -------- Getter ---------- 
  public String getPlate() { return plateNumber; }
  public Double getDebtAmount () { return debtAmount; }

}
