package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Customer {
    private String licensePlate;
    private String vehicleType;
    private boolean hasHandicappedCard;
    private String assignedSpotId;
    private LocalDateTime entryTime;

    // Formatter for storing dates in text file (e.g., 2026-02-16 10:00:00)
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Customer(String licensePlate, String vehicleType, boolean hasHandicappedCard) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.hasHandicappedCard = hasHandicappedCard;
        this.entryTime = LocalDateTime.now();
    }

    // Constructor for loading from file
    public Customer(String plate, String type, boolean isHandicap, String spotId, String timeStr) {
        this.licensePlate = plate;
        this.vehicleType = type;
        this.hasHandicappedCard = isHandicap;
        this.assignedSpotId = spotId;
        this.entryTime = LocalDateTime.parse(timeStr, TIME_FMT);
    }

    // Convert object to CSV string for FileHandler (e.g., "ABC1234,Car,false,F1-S1,2026-...")
    public String toFileString() {
        return String.join(",", 
            licensePlate, 
            vehicleType, 
            String.valueOf(hasHandicappedCard), 
            assignedSpotId, 
            entryTime.format(TIME_FMT)
        );
    }

    // Getters
    public String getLicensePlate() { return licensePlate; }
    public String getVehicleType() { return vehicleType; }
    public boolean isHandicapped() { return hasHandicappedCard; }
    public String getAssignedSpotId() { return assignedSpotId; }
    public void setAssignedSpotId(String id) { this.assignedSpotId = id; }
    public LocalDateTime getEntryTime() { return entryTime; }
}