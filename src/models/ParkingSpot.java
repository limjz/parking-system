package models;

public class ParkingSpot {
    private String id;      // e.g., "F1-R1-S1"
    private String type;    // e.g., "Regular", "Compact"
    private boolean isOccupied;

    public ParkingSpot(String id, String type, boolean isOccupied) {
        this.id = id;
        this.type = type;
        this.isOccupied = isOccupied;
    }

    // Convert to CSV for FileHandler
    public String toFileString() {
        return id + "," + type + "," + isOccupied;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }
    
    @Override
    public String toString() {
        return id + " (" + type + ")"; // For display in ComboBox
    }
}