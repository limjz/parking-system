package models;

public class ParkingStructureConfig {
    private final int floors;
    private final int spotsPerFloor;

    public ParkingStructureConfig(int floors, int spotsPerFloor) {
        this.floors = floors;
        this.spotsPerFloor = spotsPerFloor;
    }

    public int getFloors() {
        return floors;
    }

    public int getSpotsPerFloor() {
        return spotsPerFloor;
    }

    public int getTotalSpots() {
        return floors * spotsPerFloor;
    }

    public String toSpotLabel(int spotNo1ToTotal) {
        int floor = (spotNo1ToTotal - 1) / spotsPerFloor + 1;
        int spotInFloor = (spotNo1ToTotal - 1) % spotsPerFloor + 1;
        return "F" + floor + "-S" + String.format("%02d", spotInFloor);
    }
}
