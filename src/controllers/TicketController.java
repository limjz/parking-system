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
        // Note: ensuring appendData writes a new line
        FileHandler.appendLine(Config.TICKET_FILE, newTicket.toFileString());
        
        return updateSpotFile(spotID, true, plate, hasCard);
    }

    // --- 2. EXIT ---
    public boolean completeExit(Ticket ticketToExit) {
        List<String> lines = FileHandler.readAllLines(Config.TICKET_FILE);
        List<String> newLines = new ArrayList<>();
        boolean success = false;

        for (String line : lines) {
            String[] parts = line.split(Config.DELIMITER_READ);
            
            // Match Plate AND ensure ExitTime is "null" (Active Ticket)
            // Assuming Index 0 is Plate, Index 6 is ExitTime based on toFileString() order
            if (parts.length >= 7 && parts[0].equals(ticketToExit.getPlate()) && parts[6].equals("null")) {
                
                // Update the object logic
                ticketToExit.setStatus("COMPLETED");
                
                // Replace line
                newLines.add(ticketToExit.toFileString());
                
                // Free the Spot
                updateSpotFile(ticketToExit.getSpotID(), false, "null", false);
                success = true;

            } else {
                newLines.add(line);
            }
        }

        if (success) {
            FileHandler.overwriteAll(Config.TICKET_FILE, newLines);
        }
        return success;
    }
    
    // --- HELPER: Find Active Ticket Object ---
    // (You need this so ParkingController can find the ticket before Exiting)
    public Ticket getActiveTicket(String plate) {
        List<String> lines = FileHandler.readAllLines(Config.TICKET_FILE);
        for(String line : lines) {
            String[] parts = line.split(Config.DELIMITER_READ);
            // Check if matches plate and exit time is null
            if (parts.length >= 7 && parts[0].equalsIgnoreCase(plate) && parts[6].equals("null")) {
                // Recreate object: Plate, Type, Spot, Entry
                return new Ticket(parts[0], parts[1], parts[4], parts[5]);
            }
        }
        return null;
    }

    // --- HELPER: Update Spot File ---
    private boolean updateSpotFile(String targetSpotID, boolean isOccupied, String plate, boolean hasCard) {
        // NOTE: Ensure your Config.PARKINGSPOT_BASE_FILE is defined (e.g., "data/floor_")
        // Logic assumes files are named "floor_F1.txt" etc.
        String floorPrefix = targetSpotID.split("-")[0]; // "F1"
        String filename = Config.PARKINGSPOT_BASE_FILE + floorPrefix + ".txt";
        
        List<String> allLines = FileHandler.readAllLines(filename);
        List<String> newLines = new ArrayList<>();
        boolean found = false;

        for (String line : allLines) {
            String[] parts = line.split(Config.DELIMITER_READ);
            if (parts.length >= 1 && parts[0].equals(targetSpotID)) {
                // Reconstruct the spot line
                // ID;Type;Occupied;Plate;HasCard
                String newLine = String.join(";", 
                    parts[0], parts[1], String.valueOf(isOccupied), plate, String.valueOf(hasCard));
                newLines.add(newLine);
                found = true;
            } else {
                newLines.add(line);
            }
        }
        if (found) FileHandler.overwriteAll(filename, newLines);
        return found;
    }
}