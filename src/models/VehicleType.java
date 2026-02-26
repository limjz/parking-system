package models;

public enum VehicleType {
  MOTORCYCLE("Motorcycle"), 
  CAR ("Car"), 
  SUV ("SUV/Truck"), 
  HANDICAPPED ("Handicapped Vehicle");
  // BUS ("Bus"); 

  private final String displayName; 

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

  @Override
  public String toString() {
      return displayName;
  }
}
