package models;

public class ParkingSpot {
    private String id;
    private String type;
    private boolean isOccupied;
    private boolean hasHandicappedCard; // New Field

    public ParkingSpot(String id, String type, boolean isOccupied, boolean hasHandicappedCard) {
        this.id = id;
        this.type = type;
        this.isOccupied = isOccupied;
        this.hasHandicappedCard = hasHandicappedCard;
    }

    // Convert to CSV: ID,Type,Occupied,HasCard
    public String toFileString() {
        return id + "," + type + "," + isOccupied + "," + hasHandicappedCard;
    }

    // ------------ Getters ------------
    public String getId() { return id; }
    public String getType() { return type; }
    public boolean isOccupied() { return isOccupied; }
    public boolean hasHandicappedCard() { return hasHandicappedCard; }
    
    @Override
    public String toString() {
        return id + " (" + type + ")";
    }
}