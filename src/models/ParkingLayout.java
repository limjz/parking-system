package models;

public class ParkingLayout {
  private final int floors;
  private final int spotsPerFloor;
  private final int rowsPerFloor;
  private final int spotsPerRow;

  // Default configuration: 5 Floors, 20 Spots/Floor
  public ParkingLayout(int floors, int spotsPerFloor, int rowsPerFloor, int spotsPerRow) {
      this.floors = floors;
      this.spotsPerFloor = spotsPerFloor;
      this.rowsPerFloor = rowsPerFloor;
      this.spotsPerRow = spotsPerRow;
  }

  public int getFloors() { return floors; }
  public int getSpotsPerFloor() { return spotsPerFloor; }
  public int getTotalSpots() { return floors * spotsPerFloor; }

  //Converts a raw number (1..100) into a Label "F1-R1-S1"
  public String toSpotLabel(int spotNo1ToTotal) {
      int floor = (spotNo1ToTotal - 1) / spotsPerFloor + 1;
      int withinFloor = (spotNo1ToTotal - 1) % spotsPerFloor + 1;

      int row = (withinFloor - 1) / spotsPerRow + 1;
      int spot = (withinFloor - 1) % spotsPerRow + 1;

      return "F" + floor + "-R" + row + "-S" + spot;
  }
}
