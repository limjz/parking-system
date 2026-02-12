package controllers;

import java.util.ArrayList;
import java.util.List;
import models.Ticket;
import utils.Config;
import utils.FileHandler;

public class TicketController {

    // 1. ENTRY
    public boolean generateTicket(String plate, String type, boolean isPerson, boolean hasCard, String spotID) {
        Ticket newTicket = new Ticket(plate, type, isPerson, hasCard, spotID);
        FileHandler.appendData(Config.TICKET_FILE, newTicket.toFileString());
        return updateSpotFile(spotID, true, plate, hasCard);
    }

    // 2. EXIT
    public boolean completeExit(Ticket ticketToExit) {
        List<String> lines = FileHandler.readAllLines(Config.TICKET_FILE);
        List<String> newLines = new ArrayList<>();
        boolean success = false;

        for (String line : lines) {
            String[] parts = line.split(Config.DELIMITER_READ);
            
            // Match Plate AND ensure ExitTime is "null" (Active ticket)
            if (parts.length >= 6 && parts[0].equals(ticketToExit.getPlate()) && parts[6].equals("null")) {
                newLines.add(ticketToExit.toFileString()); // Update ticket with exit time
                updateSpotFile(ticketToExit.getSpotID(), false, "null", false); // Free spot
                success = true;
            } else {
                newLines.add(line);
            }
        }

        if (success) {
            FileHandler.updateData(Config.TICKET_FILE, String.join("\n", newLines));
        }
        return success;
    }

    // HELPER: Update Spot File
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
                String newLine = String.join(Config.DELIMITER_WRITE, 
                    parts[0], parts[1], String.valueOf(isOccupied), plate, String.valueOf(hasCard));
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