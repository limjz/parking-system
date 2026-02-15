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
    private double parkingFeeAmount; // normal parking fee
    private double fineAmount; // overstay fine  
    private double totalPayAmount; // fine + parking fee + debt

    private double previousDebt; 

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --- CONSTRUCTOR 1: Reading from File ---
    public Ticket(String plate, String vehicleType, boolean isHandicappedPerson, boolean hasCard, 
                  String spotID, String entryStr, String exitStr, String durStr, 
                  String payStr, String feeStr, String fineStr, String debtStr) {

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

        try { this.totalPayAmount = Double.parseDouble(payStr); } catch (Exception e) {}
        try { this.parkingFeeAmount = Double.parseDouble(feeStr); } catch (Exception e) {}
        try { this.fineAmount = Double.parseDouble(fineStr); } catch (Exception e) {}
         try { this.previousDebt = Double.parseDouble(debtStr); } catch (Exception e) {}
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

    // --- from the object convert to string and save it into .txt ---
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
            String.format("%.2f", parkingFeeAmount), 
            String.format("%.2f", fineAmount),       
            String.format("%.2f", totalPayAmount),  
            String.format("%.2f", previousDebt)
        );
    }

    // --- getter ---
    public String getPlate() { return plate; }
    public String getVehicleType() { return vehicleType; }
    public boolean isHandicappedPerson() { return isHandicappedPerson; }
    public boolean hasCard() { return hasCard; }
    public String getSpotID() { return spotID; }
    public double getParkingFeeAmount () { return parkingFeeAmount; }
    public double getFineAmount () { return fineAmount; }
    public double getTotalPayAmount() { return totalPayAmount; }
    public double getPreviousDebt () { return previousDebt; }


    public String getEntryTimeStr() { return entryTime.format(FMT); }
    public String getExitTimeStr() { return (exitTime == null) ? "-" : exitTime.format(FMT); }
    
    public String getDurationStr() { 
        if (exitTime == null) return "-";
        
        long hours = durationMinutes / 60;
        long mins = durationMinutes % 60;
        
        return hours + "h " + mins + "m";    
    }
    
    public double getHourParked () { 
        if (durationMinutes == 0)  return 0.0; 

        return Math.ceil(durationMinutes / 60.0) ; 
    }
    
    // ------ setter ------
    public void setExitTime (){ 
        this.exitTime = LocalDateTime.now(); 
        // exit time subtract entry time --> duration 
        this.durationMinutes = Duration.between(entryTime, exitTime).toMinutes(); 
        if (durationMinutes < 0){ 
            durationMinutes = 0; // time dont have negative 
        }
    }

    public void setCost (double fee, double fine, double debt){ 
        this.fineAmount = fine; 
        this.parkingFeeAmount = fee; 
        this.previousDebt = debt;
        this.totalPayAmount = fee + fine + debt; 
        
    }


    @Override
    public String toString() { return plate + " (" + spotID + ")"; }
}