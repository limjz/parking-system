package controllers;

import java.util.ArrayList;
import java.util.List;
import models.Ticket;
import utils.Config;
import utils.FileHandler;

public class TicketController {

    // --- 1. ENTRY ---
    public boolean generateTicket(String plate, String type, boolean isPerson, boolean hasCard, String spotID) {
        Ticket newTicket = new Ticket(plate, type, isPerson, hasCard, spotID);
        FileHandler.appendData(Config.TICKET_file, newTicket.toFileString());
        return updateSpotFile(spotID, true, plate, hasCard);
    }

    // --- 2. EXIT (Save & Free Spot) ---
    public boolean completeExit(Ticket ticketToExit) {
        List<String> lines = FileHandler.readAllLines(Config.TICKET_file);
        List<String> newLines = new ArrayList<>();
        boolean success = false;

        for (String line : lines) {
            String[] parts = line.split(Config.DELIMITER_READ);
            
            // Find the active ticket (Match Plate AND ensure ExitTime is "null")
            if (parts.length >= 6 && parts[0].equals(ticketToExit.getPlate()) && parts[6].equals("null")) {
                
                // Replace the old line with the NEW updated ticket string (contains exit time/duration)
                newLines.add(ticketToExit.toFileString());
                
                // Free the Parking Spot
                updateSpotFile(ticketToExit.getSpotID(), false, "null", false);
                success = true;

            } else {
                newLines.add(line);
            }
        }

        // Rewrite the ticket file
        if (success) {
            FileHandler.updateData(Config.TICKET_file, String.join("\n", newLines));
        }
        return success;
    }

    // --- HELPER: Update Spot File ---
    private boolean updateSpotFile(String targetSpotID, boolean isOccupied, String plate, boolean hasCard) {
        String floorPrefix = targetSpotID.split("-")[0];
        String filename = Config.PARKINGSPOT_BASE_FILE + floorPrefix + ".txt";
        List<String> allLines = FileHandler.readAllLines(filename);
        StringBuilder content = new StringBuilder();
        boolean found = false;

        for (String line : allLines) {
            String[] parts = line.split(Config.DELIMITER_READ);
            if (parts.length >= 2 && parts[0].equals(targetSpotID)) {
                // ID|Type|Occupied|Plate|HasCard
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