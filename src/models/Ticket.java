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
    
    // --- New Fields ---
    private LocalDateTime exitTime;
    private long durationMinutes;

    // --- CONSTRUCTOR 1: Reading from File (8 Arguments) ---
    // This is the one your ExitPage is trying to call!
    public Ticket(String plate, String vehicleType, boolean isHandicappedPerson, boolean hasCard, String spotID, String entryStr, String exitStr, String durStr) {
        this.plate = plate;
        this.vehicleType = vehicleType;
        this.isHandicappedPerson = isHandicappedPerson;
        this.hasCard = hasCard;
        this.spotID = spotID;
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parse Entry Time
        try { 
            this.entryTime = LocalDateTime.parse(entryStr, fmt); 
        } catch (Exception e) { 
            this.entryTime = LocalDateTime.now(); 
        }

        // Parse Exit Time (if exists)
        if (exitStr != null && !exitStr.equals("null") && !exitStr.isEmpty()) {
            try { 
                this.exitTime = LocalDateTime.parse(exitStr, fmt); 
            } catch (Exception e) {}
        }

        // Parse Duration (if exists)
        if (durStr != null && !durStr.equals("null") && !durStr.isEmpty()) {
            try { 
                this.durationMinutes = Long.parseLong(durStr); 
            } catch (Exception e) {}
        }
    }

    // --- CONSTRUCTOR 2: New Entry (5 Arguments) ---
    public Ticket(String plate, String vehicleType, boolean isHandicappedPerson, boolean hasCard, String spotID) {
        this(plate, vehicleType, isHandicappedPerson, hasCard, spotID, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), null, null);
    }

    // --- LOGIC: Calculate Exit ---
    public void processExit() {
        this.exitTime = LocalDateTime.now();
        this.durationMinutes = Duration.between(entryTime, exitTime).toMinutes();
        if (durationMinutes < 0) durationMinutes = 0; 
    }

    // --- Save Format ---
    public String toFileString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String exitStr = (exitTime == null) ? "null" : exitTime.format(fmt);
        
        return String.join(Config.DELIMITER_WRITE, 
            plate, 
            vehicleType, 
            String.valueOf(isHandicappedPerson), 
            String.valueOf(hasCard),
            spotID, 
            entryTime.format(fmt),
            exitStr,
            String.valueOf(durationMinutes)
        );
    }

    // --- Getters ---
    public String getPlate() { return plate; }
    public String getVehicleType() { return vehicleType; }
    public boolean isHandicappedPerson() { return isHandicappedPerson; }
    public boolean hasCard() { return hasCard; }
    public String getSpotID() { return spotID; }
    
    public String getEntryTimeStr() { return entryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); }
    
    public String getExitTimeStr() { 
        if(exitTime == null) return "-";
        return exitTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); 
    }
    
    public String getDurationStr() { 
        if(exitTime == null) return "-";
        long hours = durationMinutes / 60;
        long mins = durationMinutes % 60;
        return hours + "h " + mins + "m";
    }

    @Override
    public String toString() { return plate; }
}