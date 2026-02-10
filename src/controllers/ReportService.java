package controllers;

import models.*;

import java.util.*;

public class ReportService {

    private final TicketFileService ticketFileService;
    private final ParkingStructureConfig config;

    public ReportService(TicketFileService ticketFileService, ParkingStructureConfig config) {
        this.ticketFileService = ticketFileService;
        this.config = config;
    }

    public String generate(AdminReport.Type type) {
        return switch (type) {
            case OCCUPANCY_RATE -> occupancy();
            case REVENUE -> revenue();
            case CURRENT_VEHICLES -> currentVehicles();
            case UNPAID_FINES -> unpaidFines();
        };
    }

    public String allFloorsOverview() {
        Map<String, String> spotToPlate = buildSpotOccupancyMap();

        StringBuilder sb = new StringBuilder();
        sb.append("=== parking structure overview (")
          .append(config.getFloors()).append(" floors) ===\n\n");

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

            sb.append("floor ").append(f)
              .append(": occupied=").append(occupied)
              .append(" | available=").append(available)
              .append("\n");
        }

        sb.append("\nTOTAL: occupied=").append(totalOccupied)
          .append(" | available=").append(config.getTotalSpots() - totalOccupied)
          .append(" | spots=").append(config.getTotalSpots())
          .append("\n");

        sb.append("\nspot format: F#-R#-S# (example: F1-R1-S1)\n");

        return sb.toString();
    }

    public String floorView(int floorNumber) {
        if (floorNumber < 1 || floorNumber > config.getFloors()) {
            return "invalid floor. choose from 1 to " + config.getFloors() + ".\n";
        }

        Map<String, String> spotToPlate = buildSpotOccupancyMap();

        int start = (floorNumber - 1) * config.getSpotsPerFloor() + 1;
        int end = floorNumber * config.getSpotsPerFloor();

        StringBuilder sb = new StringBuilder();
        sb.append("=== floor ").append(floorNumber).append(" view ===\n\n");

        int occupied = 0;

        for (int spotNo = start; spotNo <= end; spotNo++) {
            String label = config.toSpotLabel(spotNo); // F1-R1-S1
            String plate = spotToPlate.get(label);

            if (plate != null && !plate.isBlank()) occupied++;

            sb.append(label).append(" | ")
              .append((plate == null || plate.isBlank()) ? "available" : "occupied (plate: " + plate + ")")
              .append("\n");
        }

        sb.append("\nsummary: occupied=").append(occupied)
          .append(" | available=").append(config.getSpotsPerFloor() - occupied)
          .append("\n");

        return sb.toString();
    }

    // ===== internal report helpers =====
    private String occupancy() {
        List<TicketRecord> tickets = ticketFileService.loadAll();
        long parked = tickets.stream().filter(TicketRecord::isParked).count();
        double rate = config.getTotalSpots() == 0 ? 0 : (parked * 100.0 / config.getTotalSpots());

        return "=== occupancy rate ===\n"
                + "parked: " + parked + "\n"
                + "total spots: " + config.getTotalSpots() + "\n"
                + "rate: " + String.format("%.2f", rate) + "%\n";
    }

    private String revenue() {
        List<TicketRecord> tickets = ticketFileService.loadAll();
        double total = tickets.stream()
                .filter(TicketRecord::isPaidOrExited)
                .mapToDouble(TicketRecord::getPaymentAmount)
                .sum();

        return "=== revenue report ===\n"
                + "total revenue: RM " + String.format("%.2f", total) + "\n";
    }

    private String currentVehicles() {
        List<TicketRecord> tickets = ticketFileService.loadAll();
        StringBuilder sb = new StringBuilder("=== current vehicles ===\n");

        int i = 1;
        for (TicketRecord t : tickets) {
            if (t.isParked()) {
                sb.append(i++).append(") plate: ").append(t.getPlate())
                  .append(" | entry: ").append(t.getEntryTime())
                  .append("\n");
            }
        }
        if (i == 1) sb.append("no vehicles currently parked.\n");
        return sb.toString();
    }

    private String unpaidFines() {
        List<TicketRecord> tickets = ticketFileService.loadAll();
        StringBuilder sb = new StringBuilder("=== unpaid fines ===\n");

        int count = 0;
        double total = 0;

        for (TicketRecord t : tickets) {
            if (t.isUnpaidFine()) {
                count++;
                total += t.getFineAmount();
                sb.append(count).append(") plate: ").append(t.getPlate())
                  .append(" | fine: RM ").append(String.format("%.2f", t.getFineAmount()))
                  .append(" | status: ").append(t.getStatus())
                  .append("\n");
            }
        }

        if (count == 0) sb.append("no unpaid fines.\n");
        else sb.append("\ntotal unpaid amount: RM ").append(String.format("%.2f", total)).append("\n");

        return sb.toString();
    }

    // spot occupancy map (use spot if provided, otherwise auto-assign)
    private Map<String, String> buildSpotOccupancyMap() {
        List<TicketRecord> parked = ticketFileService.loadAll().stream().filter(TicketRecord::isParked).toList();

        Map<String, String> spotToPlate = new HashMap<>();
        Set<String> taken = new HashSet<>();

        // explicit spot
        for (TicketRecord t : parked) {
            String spot = normalizeSpot(t.getSpot());
            if (!spot.isBlank()) {
                spotToPlate.put(spot, t.getPlate());
                taken.add(spot);
            }
        }

        // auto assign
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

        // already in format F1-R1-S1
        if (s.matches("F\\d+-R\\d+-S\\d+")) return s;

        // accept older F1-S01 and map it to row/spot if possible
        if (s.matches("F\\d+-S\\d{2}")) {
            // example: F1-S07 => treat S07 as index within floor
            try {
                String[] parts = s.split("-");
                int floor = Integer.parseInt(parts[0].substring(1));
                int withinFloor = Integer.parseInt(parts[1].substring(1)); // 1..20
                int global = (floor - 1) * config.getSpotsPerFloor() + withinFloor;
                return config.toSpotLabel(global);
            } catch (Exception ignored) {}
        }

        return "";
    }
}
