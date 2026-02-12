package controllers;

import fine.*;
import java.util.*;
import models.AdminReport;
import models.ParkingLayout; // Uses the new AdminReport model
import models.Ticket;
import utils.Config;
import utils.FileHandler;

public class AdminController {

    private final LoginController loginController;
    private final FineContext fineContext;
    private final ParkingLayout layout; // Replaces ParkingStructureConfig

    public AdminController() {
        this.loginController = new LoginController();
        
        // 1. Initialize Layout (5 Floors, 20 Spots/Floor, 4 Rows, 5 Spots/Row)
        this.layout = new ParkingLayout(5, 20, 4, 5);

        // 2. Load Fine Scheme
        FineSchemeType scheme = getFineSchemeFromFile();
        this.fineContext = new FineContext(strategyFromScheme(scheme));
    }

    // --- LOGIN ---
    public boolean isLogin(String user, String pass) {
        return loginController.authenticateAdmin(user, pass);
    }

    // --- FINE SCHEME ---
    public FineSchemeType getFineSchemeFromFile() {
        List<String> lines = FileHandler.readAllLines(Config.FINE_SCHEME_FILE);
        if (lines.isEmpty()) return FineSchemeType.FIXED;
        return FineSchemeType.fromString(lines.get(0));
    }

    public void updateFineScheme(FineSchemeType scheme) {
        fineContext.setStrategy(strategyFromScheme(scheme));
        FileHandler.updateData(Config.FINE_SCHEME_FILE, scheme.name());
    }

    private FineStrategy strategyFromScheme(FineSchemeType type) {
        return switch (type) {
            case FIXED -> new FixedFine();
            case HOURLY -> new HourlyFine();
            case PROGRESSIVE -> new ProgressiveFine();
        };
    }

    // Preview for Admin Page
    public String getFineSchemePreview() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== fine scheme preview ===\n");
        sb.append("1 hour  : RM ").append(String.format("%.2f", fineContext.calculateFine(1))).append("\n");
        sb.append("2 hours : RM ").append(String.format("%.2f", fineContext.calculateFine(2))).append("\n");
        sb.append("5 hours : RM ").append(String.format("%.2f", fineContext.calculateFine(5))).append("\n");
        return sb.toString();
    }

    // --- REPORTING (Consolidated Logic) ---

    public String generateReport(AdminReport.Type type) {
        return switch (type) {
            case OCCUPANCY_RATE -> reportOccupancy();
            case REVENUE -> reportRevenue();
            case CURRENT_VEHICLES -> reportCurrentVehicles();
            case UNPAID_FINES -> "Feature available in database mode only.";
            case PARKING_STRUCTURE -> generateAllFloorsOverview();
        };
    }

    // 1. Occupancy Report
    private String reportOccupancy() {
        long parked = loadAllTickets().stream().filter(t -> t.getExitTimeStr().equals("-")).count();
        int totalSpots = layout.getTotalSpots();
        double rate = (parked * 100.0 / totalSpots);
        return "Parked: " + parked + "\nTotal Spots: " + totalSpots + "\nRate: " + String.format("%.2f", rate) + "%";
    }

    // 2. Revenue Report
    private String reportRevenue() {
        double sum = loadAllTickets().stream().mapToDouble(Ticket::getPayAmount).sum();
        return "Total Revenue: RM " + String.format("%.2f", sum);
    }

    // 3. Current Vehicles Report
    private String reportCurrentVehicles() {
        StringBuilder sb = new StringBuilder("=== CURRENT VEHICLES ===\n");
        List<Ticket> tickets = loadAllTickets();
        boolean found = false;
        
        for (Ticket t : tickets) {
            if (t.getExitTimeStr().equals("-")) {
                sb.append("Plate: ").append(t.getPlate())
                  .append(" | Spot: ").append(t.getSpotID())
                  .append(" | Entry: ").append(t.getEntryTimeStr()).append("\n");
                found = true;
            }
        }
        if (!found) sb.append("No vehicles currently parked.");
        return sb.toString();
    }

    // 4. Floor View (e.g., F1)
    public String generateFloorView(int floorNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== FLOOR ").append(floorNumber).append(" VIEW ===\n");
        
        // Get all active tickets mapped by Spot ID
        Map<String, String> occupiedSpots = new HashMap<>();
        for (Ticket t : loadAllTickets()) {
            if (t.getExitTimeStr().equals("-")) {
                occupiedSpots.put(t.getSpotID(), t.getPlate());
            }
        }

        // Calculate spot range for this floor
        // e.g., Floor 1 is spots 1-20
        int startSpot = (floorNumber - 1) * layout.getSpotsPerFloor() + 1;
        int endSpot = floorNumber * layout.getSpotsPerFloor();

        for (int i = startSpot; i <= endSpot; i++) {
            String label = layout.toSpotLabel(i); // F1-R1-S1
            String status = occupiedSpots.containsKey(label) ? "[OCCUPIED: " + occupiedSpots.get(label) + "]" : "[ ]";
            sb.append(label).append(" : ").append(status).append("\n");
        }
        
        return sb.toString();
    }

    // 5. All Floors Overview
    public String generateAllFloorsOverview() {
        return reportOccupancy() + "\n\n(Use 'Parking Structure' buttons to see detailed floor maps)";
    }

    // --- HELPER: Load Data ---
    private List<Ticket> loadAllTickets() {
        List<String> lines = FileHandler.readAllLines(Config.TICKET_FILE);
        List<Ticket> tickets = new ArrayList<>();
        
        for (String line : lines) {
            try {
                String[] p = line.split(Config.DELIMITER_READ);
                if (p.length < 6) continue;
                // Reconstruct Ticket from File
                String exit = (p.length > 6) ? p[6] : "null";
                String dur = (p.length > 7) ? p[7] : "0";
                String pay = (p.length > 8) ? p[8] : "0";

                tickets.add(new Ticket(p[0], p[1], Boolean.parseBoolean(p[2]), 
                                       Boolean.parseBoolean(p[3]), p[4], p[5], exit, dur, pay));
            } catch (Exception e) {}
        }
        return tickets;
    }
}