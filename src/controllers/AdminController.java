package controllers;

import fine.*;
import utils.SimpleFile;

import java.util.*;

public class AdminController {

    // Admin login 
    private final String adminUser = "admin";
    private final String adminPass = "1234";

    // Files 
    private final String ticketFilePath = "data/ticket.txt";
    private final String fineSchemeFilePath = "data/fine_scheme.txt";

    // Parking structure config 
    private final int FLOORS = 5;
    private final int SPOTS_PER_FLOOR = 20;
    private final int TOTAL_SPOTS = FLOORS * SPOTS_PER_FLOOR;

    // Strategy Context 
    private final FineContext fineContext;

    public AdminController() {
        FineSchemeType scheme = getFineSchemeFromFile();
        this.fineContext = new FineContext(createStrategy(scheme));
    }

    // LOGIN 
    public boolean login(String user, String pass) {
        return adminUser.equals(user) && adminPass.equals(pass);
    }

    // STRATEGY 
    private FineStrategy createStrategy(FineSchemeType type) {
        return switch (type) {
            case FIXED -> new FixedFine();
            case HOURLY -> new HourlyFine();
            case PROGRESSIVE -> new ProgressiveFine();
        };
    }

    public FineSchemeType getFineSchemeFromFile() {
        List<String> lines = SimpleFile.readAllLines(fineSchemeFilePath);
        if (lines.isEmpty()) return FineSchemeType.FIXED;
        return FineSchemeType.fromString(lines.get(0));
    }

    public String getCurrentFineScheme() {
        return fineContext.getCurrentScheme();
    }

    public void updateFineScheme(FineSchemeType scheme) {
        fineContext.setStrategy(createStrategy(scheme));
        SimpleFile.writeText(fineSchemeFilePath, scheme.name(), false);
    }

    // REPORT API 
    public String generateReport(ReportType type) {
        return switch (type) {
            case OCCUPANCY_RATE -> reportOccupancy();
            case PARKING_STRUCTURE -> reportParkingStructureSummary();
            case REVENUE -> reportRevenue();
            case CURRENT_VEHICLES -> reportCurrentVehicles();
            case UNPAID_FINES -> reportUnpaidFines();
        };
    }

    // NEW: floor-by-floor view
    public String generateFloorView(int floorNumber) {
        if (floorNumber < 1 || floorNumber > FLOORS) {
            return "Invalid floor. Choose from 1 to " + FLOORS + ".\n";
        }

        // Build occupancy map: spotLabel -> plate
        Map<String, String> spotToPlate = buildSpotOccupancyMap();

        int startSpot = (floorNumber - 1) * SPOTS_PER_FLOOR + 1;
        int endSpot = floorNumber * SPOTS_PER_FLOOR;

        StringBuilder sb = new StringBuilder();
        sb.append("=== FLOOR ").append(floorNumber).append(" VIEW ===\n");
        sb.append("Spots: ").append(SPOTS_PER_FLOOR).append(" (")
          .append("F").append(floorNumber).append("-S01 to ")
          .append("F").append(floorNumber).append("-S").append(String.format("%02d", SPOTS_PER_FLOOR))
          .append(")\n\n");

        int occupiedCount = 0;

        for (int spotNo = startSpot; spotNo <= endSpot; spotNo++) {
            String spotLabel = toSpotLabel(spotNo); // e.g., F2-S03
            String plate = spotToPlate.get(spotLabel);

            if (plate != null && !plate.isBlank()) occupiedCount++;

            sb.append(spotLabel)
              .append(" | ")
              .append((plate == null || plate.isBlank()) ? "AVAILABLE" : ("OCCUPIED (Plate: " + plate + ")"))
              .append("\n");
        }

        int available = SPOTS_PER_FLOOR - occupiedCount;
        sb.append("\nSummary: OCCUPIED=").append(occupiedCount)
          .append(" | AVAILABLE=").append(available).append("\n");

        return sb.toString();
    }

    public String generateAllFloorsOverview() {
        Map<String, String> spotToPlate = buildSpotOccupancyMap();

        StringBuilder sb = new StringBuilder();
        sb.append("=== PARKING STRUCTURE OVERVIEW (5 FLOORS) ===\n\n");

        int totalOccupied = 0;

        for (int f = 1; f <= FLOORS; f++) {
            int startSpot = (f - 1) * SPOTS_PER_FLOOR + 1;
            int endSpot = f * SPOTS_PER_FLOOR;

            int occupied = 0;
            for (int spotNo = startSpot; spotNo <= endSpot; spotNo++) {
                String label = toSpotLabel(spotNo);
                String plate = spotToPlate.get(label);
                if (plate != null && !plate.isBlank()) occupied++;
            }

            totalOccupied += occupied;
            int available = SPOTS_PER_FLOOR - occupied;

            sb.append("Floor ").append(f)
              .append(": OCCUPIED=").append(occupied)
              .append(" | AVAILABLE=").append(available)
              .append("\n");
        }

        int totalAvailable = TOTAL_SPOTS - totalOccupied;
        sb.append("\nTOTAL: OCCUPIED=").append(totalOccupied)
          .append(" | AVAILABLE=").append(totalAvailable)
          .append(" | SPOTS=").append(TOTAL_SPOTS)
          .append("\n");

        sb.append("\nTip: Click Floor 1–5 buttons to view individual spot status.\n");

        return sb.toString();
    }

    // INTERNAL HELPERS
    private List<String[]> loadTickets() {
        List<String> lines = SimpleFile.readAllLines(ticketFilePath);
        List<String[]> result = new ArrayList<>();

        for (String line : lines) {
            if (line == null) continue;
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] p = line.contains("|") ? line.split("\\|") : line.split(",");
            for (int i = 0; i < p.length; i++) p[i] = p[i].trim();
            result.add(p);
        }
        return result;
    }

    private double toDouble(String s) {
        try { return Double.parseDouble(s); }
        catch (Exception e) { return 0; }
    }

    private String toSpotLabel(int spotNo1ToTotal) {
        // Convert 1..100 -> F1-S01..F5-S20
        int floor = (spotNo1ToTotal - 1) / SPOTS_PER_FLOOR + 1;
        int spotInFloor = (spotNo1ToTotal - 1) % SPOTS_PER_FLOOR + 1;
        return "F" + floor + "-S" + String.format("%02d", spotInFloor);
    }

    private Map<String, String> buildSpotOccupancyMap() {
        // Returns map: "F1-S01" -> "ABC1234" for parked cars
        // If your ticket file contains a spot column, we will use it.
        // Otherwise we auto-assign parked vehicles sequentially from F1-S01 upward.

        List<String[]> tickets = loadTickets();

        List<String[]> parked = new ArrayList<>();
        for (String[] t : tickets) {
            // expected: [0]=id [1]=plate [2]=entry [3]=exit [4]=payment [5]=status [6]=fine [7]=spot(optional)
            if (t.length > 5 && "PARKED".equalsIgnoreCase(t[5])) {
                parked.add(t);
            }
        }

        Map<String, String> spotToPlate = new HashMap<>();

        // First pass: use explicit spot if available
        Set<String> taken = new HashSet<>();
        for (String[] t : parked) {
            if (t.length > 7) {
                String spot = t[7];
                String plate = (t.length > 1) ? t[1] : "";
                if (spot != null && !spot.isBlank()) {
                    String normalized = normalizeSpot(spot);
                    if (!normalized.isBlank()) {
                        spotToPlate.put(normalized, plate);
                        taken.add(normalized);
                    }
                }
            }
        }

        // Second pass: auto-assign remaining parked vehicles to free spots
        int cursor = 1;
        for (String[] t : parked) {
            String plate = (t.length > 1) ? t[1] : "";
            boolean hasSpot = (t.length > 7 && t[7] != null && !t[7].isBlank());
            if (hasSpot) continue;

            while (cursor <= TOTAL_SPOTS) {
                String label = toSpotLabel(cursor);
                cursor++;
                if (!taken.contains(label) && !spotToPlate.containsKey(label)) {
                    spotToPlate.put(label, plate);
                    taken.add(label);
                    break;
                }
            }
        }

        return spotToPlate;
    }

    private String normalizeSpot(String spotRaw) {
        // Accepts formats like:
        // "F1-S01" or "1-01" or "F1S01" or "Floor1Spot01" (best-effort)
        if (spotRaw == null) return "";
        String s = spotRaw.trim().toUpperCase();

        // Already correct
        if (s.matches("F[1-5]-S\\d{2}")) return s;

        // Common "1-01" format
        if (s.matches("[1-5]-\\d{2}")) {
            String[] p = s.split("-");
            return "F" + p[0] + "-S" + p[1];
        }

        // If it's just a number 1..100 (spot index)
        if (s.matches("\\d+")) {
            int n;
            try { n = Integer.parseInt(s); } catch (Exception e) { return ""; }
            if (n >= 1 && n <= TOTAL_SPOTS) return toSpotLabel(n);
        }

        return "";
    }

    // REPORT IMPLEMENTATIONS 
    private String reportOccupancy() {
        int parked = 0;
        for (String[] t : loadTickets()) {
            if (t.length > 5 && "PARKED".equalsIgnoreCase(t[5])) parked++;
        }
        double rate = (TOTAL_SPOTS == 0) ? 0 : (parked * 100.0 / TOTAL_SPOTS);

        return "=== OCCUPANCY RATE ===\n"
                + "Parked: " + parked + "\n"
                + "Total Spots: " + TOTAL_SPOTS + " (" + FLOORS + " floors × " + SPOTS_PER_FLOOR + " spots)\n"
                + "Rate: " + String.format("%.2f", rate) + "%\n";
    }

    private String reportRevenue() {
        double sum = 0;
        for (String[] t : loadTickets()) {
            if (t.length > 5 && (t[5].equalsIgnoreCase("PAID") || t[5].equalsIgnoreCase("EXITED"))) {
                if (t.length > 4) sum += toDouble(t[4]);
            }
        }
        return "=== REVENUE REPORT ===\nTotal Revenue: RM " + String.format("%.2f", sum) + "\n";
    }

    private String reportCurrentVehicles() {
        StringBuilder sb = new StringBuilder("=== CURRENT VEHICLES ===\n");
        int i = 1;
        for (String[] t : loadTickets()) {
            if (t.length > 5 && t[5].equalsIgnoreCase("PARKED")) {
                String plate = (t.length > 1) ? t[1] : "";
                String entry = (t.length > 2) ? t[2] : "";
                sb.append(i++).append(") Plate: ").append(plate).append(" | Entry: ").append(entry).append("\n");
            }
        }
        if (i == 1) sb.append("No vehicles currently parked.\n");
        return sb.toString();
    }

    private String reportUnpaidFines() {
        StringBuilder sb = new StringBuilder("=== UNPAID FINES ===\n");
        int count = 0;
        for (String[] t : loadTickets()) {
            if (t.length > 6 && t[5].equalsIgnoreCase("UNPAID")) {
                String plate = (t.length > 1) ? t[1] : "";
                String fine = t[6];
                count++;
                sb.append(count).append(") Plate: ").append(plate).append(" | Fine: RM ").append(fine).append("\n");
            }
        }
        if (count == 0) sb.append("No unpaid fines.\n");
        return sb.toString();
    }

    private String reportParkingStructureSummary() {
        // Updated to show floor overview and instruct using floor buttons
        return generateAllFloorsOverview();
    }
}
