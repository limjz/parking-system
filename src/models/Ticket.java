package models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import utils.Config;

public class Ticket {
    private String plate;
    private String vehicleType;
    private boolean isHandicappedPerson;
    private boolean hasCard;
    private String spotID;
    private LocalDateTime entryTime;
    
    // Exit fields
    private LocalDateTime exitTime;
    private long durationMinutes;
    private double payAmount; 

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --- CONSTRUCTOR 1: Reading from File ---
    public Ticket(String plate, String vehicleType, boolean isHandicappedPerson, boolean hasCard, 
                  String spotID, String entryStr, String exitStr, String durStr, String payStr) {
        this.plate = plate;
        this.vehicleType = vehicleType;
        this.isHandicappedPerson = isHandicappedPerson;
        this.hasCard = hasCard;
        this.spotID = spotID;
        
        try { this.entryTime = LocalDateTime.parse(entryStr, FMT); } 
        catch (Exception e) { this.entryTime = LocalDateTime.now(); }

        if (exitStr != null && !exitStr.equals("null") && !exitStr.equals("-")) {
            try { this.exitTime = LocalDateTime.parse(exitStr, FMT); } catch (Exception e) {}
        }
        
        if (durStr != null && !durStr.equals("null")) {
            try { this.durationMinutes = Long.parseLong(durStr); } catch (Exception e) {}
        }

        if (payStr != null && !payStr.equals("null")) {
             try { this.payAmount = Double.parseDouble(payStr); } catch (Exception e) {}
        }
    }

    // --- CONSTRUCTOR 2: New Entry ---
    public Ticket(String plate, String vehicleType, boolean isHandicappedPerson, boolean hasCard, String spotID) {
        this.plate = plate;
        this.vehicleType = vehicleType;
        this.isHandicappedPerson = isHandicappedPerson;
        this.hasCard = hasCard;
        this.spotID = spotID;
        this.entryTime = LocalDateTime.now();
    }

    // --- LOGIC ---
    public void processExit() {
        this.exitTime = LocalDateTime.now();
        this.durationMinutes = Duration.between(entryTime, exitTime).toMinutes();
        if (durationMinutes < 0) durationMinutes = 0;
        // Simple fee logic (Admin Strategy overrides this in reports, but UI needs immediate feedback)
        this.payAmount = (durationMinutes / 60.0 + 1) * 2.0; 
    }

    // --- SAVE FORMAT (Pipe Delimited) ---
    public String toFileString() {
        String exitStr = (exitTime == null) ? "null" : exitTime.format(FMT);
        
        return String.join(Config.DELIMITER_WRITE, 
            plate, 
            vehicleType, 
            String.valueOf(isHandicappedPerson), 
            String.valueOf(hasCard),
            spotID, 
            entryTime.format(FMT),
            exitStr,
            String.valueOf(durationMinutes),
            String.format("%.2f", payAmount)
        );
    }

    // --- GETTERS ---
    public String getPlate() { return plate; }
    public String getVehicleType() { return vehicleType; }
    public boolean isHandicappedPerson() { return isHandicappedPerson; }
    public boolean hasCard() { return hasCard; }
    public String getSpotID() { return spotID; }
    public double getPayAmount() { return payAmount; }
    
    public String getEntryTimeStr() { return entryTime.format(FMT); }
    public String getExitTimeStr() { return (exitTime == null) ? "-" : exitTime.format(FMT); }
    public String getDurationStr() { 
        if(exitTime == null) return "-";
        return (durationMinutes / 60) + "h " + (durationMinutes % 60) + "m";
    }


    @Override
    public String toString() { return plate + " (" + spotID + ")"; }
}