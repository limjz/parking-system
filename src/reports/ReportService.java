package reports;

import models.*;
import java.util.*;

public class ReportService {

    private final TicketRepository repo;
    private final ParkingStructureConfig config;

    public ReportService(TicketRepository repo, ParkingStructureConfig config) {
        this.repo = repo;
        this.config = config;
    }

    public String generate(ReportType type) {
        return switch (type) {
            case OCCUPANCY_RATE -> occupancy();
            case PARKING_STRUCTURE -> allFloorsOverview();
            case REVENUE -> revenue();
            case CURRENT_VEHICLES -> currentVehicles();
            case UNPAID_FINES -> unpaidFines();
        };
    }

    public String allFloorsOverview() {
        Map<String, String> spotToPlate = buildSpotOccupancyMap();

        StringBuilder sb = new StringBuilder();
        sb.append("=== PARKING STRUCTURE OVERVIEW (")
          .append(config.getFloors()).append(" FLOORS) ===\n\n");

        int totalOccupied = 0;

        for (int f = 1; f <= config.getFloors(); f++) {
            int start = (f - 1) * config.getSpotsPerFloor() + 1;
            int end = f * config.getSpotsPerFloor();

            int occupied = 0;
            for (int s = start; s <= end; s++) {
                String label = config.toSpotLabel(s);
                if (spotToPlate.containsKey(label)) occupied++;
            }

            totalOccupied += occupied;
            int available = config.getSpotsPerFloor() - occupied;

            sb.append("Floor ").append(f)
              .append(": OCCUPIED=").append(occupied)
              .append(" | AVAILABLE=").append(available)
              .append("\n");
        }

        sb.append("\nTOTAL: OCCUPIED=").append(totalOccupied)
          .append(" | AVAILABLE=").append(config.getTotalSpots() - totalOccupied)
          .append(" | SPOTS=").append(config.getTotalSpots())
          .append("\n");

        sb.append("\nTip: Use Floor buttons to view individual spot status.\n");
        return sb.toString();
    }

    public String floorView(int floorNumber) {
        if (floorNumber < 1 || floorNumber > config.getFloors()) {
            return "Invalid floor. Choose from 1 to " + config.getFloors() + ".\n";
        }

        Map<String, String> spotToPlate = buildSpotOccupancyMap();

        int start = (floorNumber - 1) * config.getSpotsPerFloor() + 1;
        int end = floorNumber * config.getSpotsPerFloor();

        StringBuilder sb = new StringBuilder();
        sb.append("=== FLOOR ").append(floorNumber).append(" VIEW ===\n\n");

        int occupied = 0;

        for (int spotNo = start; spotNo <= end; spotNo++) {
            String label = config.toSpotLabel(spotNo);
            String plate = spotToPlate.get(label);

            if (plate != null && !plate.isBlank()) occupied++;

            sb.append(label).append(" | ")
              .append((plate == null || plate.isBlank()) ? "AVAILABLE" : "OCCUPIED (Plate: " + plate + ")")
              .append("\n");
        }

        sb.append("\nSummary: OCCUPIED=").append(occupied)
          .append(" | AVAILABLE=").append(config.getSpotsPerFloor() - occupied)
          .append("\n");

        return sb.toString();
    }

    private String occupancy() {
        List<TicketRecord> tickets = repo.findAll();
        long parked = tickets.stream().filter(TicketRecord::isParked).count();
        double rate = config.getTotalSpots() == 0 ? 0 : (parked * 100.0 / config.getTotalSpots());

        return "=== OCCUPANCY RATE ===\n"
                + "Parked: " + parked + "\n"
                + "Total Spots: " + config.getTotalSpots() + "\n"
                + "Rate: " + String.format("%.2f", rate) + "%\n";
    }

    private String revenue() {
        List<TicketRecord> tickets = repo.findAll();
        double total = tickets.stream()
                .filter(TicketRecord::isPaidOrExited)
                .mapToDouble(TicketRecord::getPaymentAmount)
                .sum();

        return "=== REVENUE REPORT ===\nTotal Revenue: RM " + String.format("%.2f", total) + "\n";
    }

    private String currentVehicles() {
        List<TicketRecord> tickets = repo.findAll();
        StringBuilder sb = new StringBuilder("=== CURRENT VEHICLES ===\n");

        int i = 1;
        for (TicketRecord t : tickets) {
            if (t.isParked()) {
                sb.append(i++).append(") Plate: ").append(t.getPlate())
                  .append(" | Entry: ").append(t.getEntryTime())
                  .append("\n");
            }
        }
        if (i == 1) sb.append("No vehicles currently parked.\n");
        return sb.toString();
    }

    private String unpaidFines() {
        List<TicketRecord> tickets = repo.findAll();
        StringBuilder sb = new StringBuilder("=== UNPAID FINES ===\n");

        int count = 0;
        double total = 0;

        for (TicketRecord t : tickets) {
            if (t.isUnpaidFine()) {
                count++;
                total += t.getFineAmount();
                sb.append(count).append(") Plate: ").append(t.getPlate())
                  .append(" | Fine: RM ").append(String.format("%.2f", t.getFineAmount()))
                  .append(" | Status: ").append(t.getStatus())
                  .append("\n");
            }
        }

        if (count == 0) sb.append("No unpaid fines.\n");
        else sb.append("\nTotal Unpaid Amount: RM ").append(String.format("%.2f", total)).append("\n");

        return sb.toString();
    }

    // Build spot occupancy map using ticket spot if present; otherwise auto-assign.
    private Map<String, String> buildSpotOccupancyMap() {
        List<TicketRecord> parked = repo.findAll().stream().filter(TicketRecord::isParked).toList();

        Map<String, String> spotToPlate = new HashMap<>();
        Set<String> taken = new HashSet<>();

        // 1) explicit spot
        for (TicketRecord t : parked) {
            String spot = normalizeSpot(t.getSpot());
            if (!spot.isBlank()) {
                spotToPlate.put(spot, t.getPlate());
                taken.add(spot);
            }
        }

        // 2) auto-assign
        int cursor = 1;
        for (TicketRecord t : parked) {
            if (!normalizeSpot(t.getSpot()).isBlank()) continue;

            while (cursor <= config.getTotalSpots()) {
                String label = config.toSpotLabel(cursor++);
                if (!taken.contains(label)) {
                    spotToPlate.put(label, t.getPlate());
                    taken.add(label);
                    break;
                }
            }
        }

        return spotToPlate;
    }

    private String normalizeSpot(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toUpperCase();
        if (s.matches("F[1-9]-S\\d{2}")) return s;
        if (s.matches("[1-9]-\\d{2}")) {
            String[] p = s.split("-");
            return "F" + p[0] + "-S" + p[1];
        }
        if (s.matches("\\d+")) {
            try {
                int n = Integer.parseInt(s);
                if (n >= 1 && n <= config.getTotalSpots()) return config.toSpotLabel(n);
            } catch (Exception ignored) {}
        }
        return "";
    }
}
