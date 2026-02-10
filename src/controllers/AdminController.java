package controllers;

import fine.*;
import models.*;
import utils.SimpleFile;

public class AdminController {

    private final LoginController loginController;
    private final FineContext fineContext;

    private final TicketFileService ticketFileService;
    private final ParkingStructureConfig config;
    private final ReportService reportService;

    private final String fineSchemeFilePath = "data/fine_scheme.txt";

    public AdminController() {
        this.loginController = new LoginController();

        // Load saved fine scheme
        FineSchemeType scheme = getFineSchemeFromFile();
        this.fineContext = new FineContext(strategyFromScheme(scheme));

        // Ticket file service
        this.ticketFileService = new TicketFileService("data/ticket.txt");

        // 5 floors, 20 spots per floor, 4 rows per floor, 5 spots per row
        this.config = new ParkingStructureConfig(5, 20, 4, 5);

        // Report service
        this.reportService = new ReportService(ticketFileService, config);
    }

    // LOGIN
    public boolean login(String user, String pass) {
        return loginController.authenticateAdmin(user, pass);
    }

    // FINE SCHEME

    public FineSchemeType getFineSchemeFromFile() {
        var lines = SimpleFile.readAllLines(fineSchemeFilePath);
        if (lines.isEmpty()) return FineSchemeType.FIXED;
        return FineSchemeType.fromString(lines.get(0));
    }

    public void updateFineScheme(FineSchemeType scheme) {
        fineContext.setStrategy(strategyFromScheme(scheme));
        SimpleFile.writeText(fineSchemeFilePath, scheme.name(), false);
    }

    private FineStrategy strategyFromScheme(FineSchemeType type) {
        return switch (type) {
            case FIXED -> new FixedFine();
            case HOURLY -> new HourlyFine();
            case PROGRESSIVE -> new ProgressiveFine();
        };
    }

    // Dummy preview 
    public String getFineSchemePreview() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== fine scheme preview ===\n");
        sb.append("1 hour  : RM ").append(String.format("%.2f", fineContext.calculateFine(1))).append("\n");
        sb.append("2 hours : RM ").append(String.format("%.2f", fineContext.calculateFine(2))).append("\n");
        sb.append("5 hours : RM ").append(String.format("%.2f", fineContext.calculateFine(5))).append("\n");
        return sb.toString();
    }

    // REPORTS

    public String generateReport(AdminReport.Type type) {
        return reportService.generate(type);
    }

    public String generateAllFloorsOverview() {
        return reportService.allFloorsOverview();
    }

    public String generateFloorView(int floorNumber) {
        return reportService.floorView(floorNumber);
    }
}
