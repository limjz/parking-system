package controllers;

import fine.*;
import java.util.ArrayList;
import java.util.List;
import models.Ticket;
import utils.Config;
import utils.FileHandler;


public class TicketController {

    private final FineController fineController = new FineController ();
    private final DebtController debtController = new DebtController ();


    public List<Ticket> getAllTickets() {
        List<String> lines = FileHandler.readAllLines(Config.TICKET_FILE);
        List<Ticket> tickets = new ArrayList<>();
        
        for (String line : lines) {
            try {
                String[] p = line.split(Config.DELIMITER_READ);
                if (p.length < 6) continue;
                
                // Reconstruct Ticket from File
                String exit = (p.length > 6) ? p[6] : "null";
                String duration = (p.length > 7) ? p[7] : "0";
                String fee = (p.length > 8) ? p[8] : "0";
                String fine = (p.length > 9) ? p[9] : "0";
                String pay = (p.length > 10) ? p[10] : "0";

                tickets.add(new Ticket(p[0], p[1], Boolean.parseBoolean(p[2]), 
                                       Boolean.parseBoolean(p[3]), p[4], p[5], exit, duration, fee, fine, pay));
            } catch (Exception e) {}
        }
        return tickets;
    }

    // ENTRY
    public boolean generateTicket(String plate, String type, boolean isPerson, boolean hasCard, String spotID) {
        Ticket newTicket = new Ticket(plate, type, isPerson, hasCard, spotID);
        FileHandler.appendData(Config.TICKET_FILE, newTicket.toFileString());
        return updateSpotFile(spotID, true, plate, hasCard);
    }

    //calculate fine and parking fee
    public void processTicketExit (Ticket ticket) { 
        ticket.setExitTime (); 

        double hours = Math.ceil(ticket.getHourParked()); 
        if (hours == 0) { 
            hours = 1; 
        }

        double hourRate = getSpotRate( ticket.getSpotID(), ticket.hasCard()); 
        double standardFee = hours * hourRate; 

        double fine = 0.0; //init fine equals to zero

        // overstay fine calculate
        if (hours > 24){ 
            FineSchemeType currentScheme = fineController.getCurrentScheme(); 
            FineStrategy strategy = fineController.createStrategy(currentScheme);
            fine = strategy.calculateFine(hours); 

        }

        // check if got debt in outstanding_fines.txt
        double oldDebt = debtController.getDebtAmount(ticket.getPlate()); 

        // sum up all the payment amount
        ticket.setCost(standardFee, fine, oldDebt);

    }


    // EXIT
    public boolean completeExit(Ticket ticketToExit) {
        List<String> lines = FileHandler.readAllLines(Config.TICKET_FILE);
        List<String> newLines = new ArrayList<>();
        boolean success = false;

        for (String line : lines) {
            String[] parts = line.split(Config.DELIMITER_READ);
            
            // Match Plate AND ensure ExitTime is "null" (Active ticket)
            if (parts.length >= 6 && parts[0].equals(ticketToExit.getPlate()) && parts[6].equals("null")) {
                
                newLines.add(ticketToExit.toFileString()); // Update ticket with exit time and fee & fine
                updateSpotFile(ticketToExit.getSpotID(), false, "null", false); // Free spot
                success = true;

            } 
            else {
                newLines.add(line);
            }
        }

        if (success) {
            FileHandler.updateData(Config.TICKET_FILE, String.join("\n", newLines));
        }
        return success;
    }

    private double getSpotRate (String spotID, boolean hasHandicapedCard)
    { 
        String floorPrefix = spotID.split("-")[0]; // "F1"
        String filename = Config.PARKINGSPOT_BASE_FILE + floorPrefix + ".txt"; // target to specific floor 
        List <String> lines = FileHandler.readAllLines(filename);

        String spotType = "Regular"; 

        for (String line : lines){ 
            // check if the db is start from spotID (F1-R1-S1)
            if (line.startsWith(spotID))
            { 
                String [] p = line.split(Config.DELIMITER_READ); 
                
                if (p.length > 1) 
                { 
                    spotType = p[1]; // second object is the type 
                    break; 
                }
            }
        }

        if (hasHandicapedCard) {
            if (spotType.equalsIgnoreCase("Handicapped")) {
                return 0.0; // Free if parked in designated handicapped spot
            } else {
                return 2.0; // Flat RM 2.00 if parked anywhere else
            }
        }

        return switch (spotType) { 
            case "Compact" -> 2.0;
            case "Regular" -> 5.0;
            case "Handicapped" -> 2.0; // Fallback rate (though logic restricts this)
            case "Reserved" -> 10.0;
            default -> 5.0;
        };

    }



    // HELPER: Update parkingSpot_ File
    private boolean updateSpotFile(String targetSpotID, boolean isOccupied, String plate, boolean hasCard) {
        String floorPrefix = targetSpotID.split("-")[0]; // "F1"
        String filename = Config.PARKINGSPOT_BASE_FILE + floorPrefix + ".txt";
        
        List<String> allLines = FileHandler.readAllLines(filename);
        StringBuilder content = new StringBuilder();
        boolean found = false;

        for (String line : allLines) {
            String[] parts = line.split(Config.DELIMITER_READ);
            if (parts.length >= 1 && parts[0].equals(targetSpotID)) {
                // Keep Type (parts[1]), Update Status/Plate
                // Format: ID|Type|Occupied|Plate|HasCard
                String newLine = String.join(Config.DELIMITER_WRITE, parts[0], parts[1], String.valueOf(isOccupied), plate, String.valueOf(hasCard));
                content.append(newLine).append("\n");
                found = true;
            } else {
                content.append(line).append("\n");
            }
        }
        if (found) FileHandler.updateData(filename, content.toString().trim());
        return found;
    }
    
    public boolean isVip(String plate) {
        List<String> vipList = FileHandler.readAllLines(Config.VIP_FILE);
        for (String vip : vipList) {
            if (vip.trim().equalsIgnoreCase(plate.trim())) return true;
        }
        return false;
    }

}