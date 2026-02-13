package controllers;

import fine.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.AdminReport;
import models.Debt;
import models.ParkingLayout; // Uses the new AdminReport model
import models.Ticket;

public class AdminController {

    private final LoginController loginController = new LoginController();
    private final FineController fineController = new FineController(); 
    private final TicketController ticketController = new TicketController(); 
    private final DebtController debtController = new DebtController();
    private final FineContext fineContext;
    private final ParkingLayout layout; // Replaces ParkingStructureConfig

    public AdminController() {

        //  Initialize Layout (5 Floors, 20 Spots per floor, 4 Rows, 5 Spots per row)
        this.layout = new ParkingLayout(5, 20, 4, 5);

        // Load Fine Scheme
        FineSchemeType scheme = fineController.getCurrentScheme();
        this.fineContext = new FineContext(fineController.createStrategy(scheme));
    }

    // --- LOGIN ---
    public boolean isLogin(String user, String pass) {
        return loginController.authenticateAdmin(user, pass);
    }

    // --- FINE SCHEME ---
    public FineSchemeType getFineSchemeFromFile() {
        return fineController.getCurrentScheme();
    }

    public void updateFineScheme(FineSchemeType scheme) {
        fineContext.setStrategy(fineController.createStrategy(scheme));
        fineController.saveScheme(scheme);;
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
            case UNPAID_FINES -> reportUnpaidFines();
            case PARKING_STRUCTURE -> generateAllFloorsOverview();
        };
    }

    // Occupancy Report
    private String reportOccupancy() {
        long parked = ticketController.getAllTickets().stream().filter(t -> t.getExitTimeStr().equals("-")).count();
        int totalSpots = layout.getTotalSpots();
        double rate = (parked * 100.0 / totalSpots);
        return "Parked: " + parked + "\nTotal Spots: " + totalSpots + "\nRate: " + String.format("%.2f", rate) + "%";
    }

    // 2. Revenue Report
    private String reportRevenue() {
        double sum = ticketController.getAllTickets().stream().mapToDouble(Ticket::getTotalPayAmount).sum();
        return "Total Revenue: RM " + String.format("%.2f", sum);
    }

    //  Current Vehicles Report
    private String reportCurrentVehicles() {
        StringBuilder sb = new StringBuilder("=== CURRENT VEHICLES ===\n");
        List<Ticket> tickets = ticketController.getAllTickets();
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


    private String reportUnpaidFines (){ 
        List<Debt> allDebts = debtController.getAllDebts(); 

        if (allDebts.isEmpty()) {
            return "No outstanding fines found.";
        }

        StringBuilder sb = new StringBuilder("=== OUTSTANDING FINES REPORT ===\n\n");
        sb.append(String.format("%-15s | %-10s\n", "License Plate", "Amount"));
        sb.append("-----------------------------\n");

        double totalDebt = 0.0;

        // 3. Loop through the Debt objects
        for (Debt d : allDebts) {
            sb.append(String.format("%-15s | RM %7.2f\n", d.getPlate(), d.getDebtAmount()));
            totalDebt += d.getDebtAmount();
        }

        sb.append("-----------------------------\n");
        sb.append(String.format("TOTAL OUTSTANDING: RM %.2f", totalDebt));
        
        return sb.toString();
    }

    // Floor View (e.g., F1)
    public String generateFloorView(int floorNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== FLOOR ").append(floorNumber).append(" VIEW ===\n");
        
        // Get all active tickets mapped by Spot ID
        Map<String, String> occupiedSpots = new HashMap<>();
        for (Ticket t : ticketController.getAllTickets()) {
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


}