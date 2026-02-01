package controllers;

import fine.*;
import models.*;
import reports.*;
import utils.SimpleFile;

public class AdminController {

    private final LoginController loginController;
    private final FineContext fineContext;
    private final ReportService reportService;

    private final String fineSchemeFilePath = "data/fine_scheme.txt";

    public AdminController() {
        this.loginController = new LoginController();

        // Fine strategy setup
        FineSchemeType scheme = getFineSchemeFromFile();
        this.fineContext = new FineContext(strategyFromScheme(scheme));

        // Data + reports
        TicketRepository repo = new FileTicketRepository("data/ticket.txt");
        ParkingStructureConfig config = new ParkingStructureConfig(5, 20); // 5 floors, 20 spots each
        this.reportService = new ReportService(repo, config);
    }

    // ========== LOGIN ==========
    public boolean login(String user, String pass) {
        return loginController.authenticateAdmin(user, pass);
    }

    // ========== FINE SCHEME (Strategy Pattern) ==========
    public FineSchemeType getFineSchemeFromFile() {
        var lines = SimpleFile.readAllLines(fineSchemeFilePath);
        if (lines.isEmpty()) return FineSchemeType.FIXED;
        return FineSchemeType.fromString(lines.get(0));
    }

    public String getCurrentFineScheme() {
        return fineContext.getCurrentScheme();
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

    // ========== REPORTS ==========
    public String generateReport(ReportType type) {
        return reportService.generate(type);
    }

    // Parking Structure Views
    public String generateAllFloorsOverview() {
        return reportService.allFloorsOverview();
    }

    public String generateFloorView(int floorNumber) {
        return reportService.floorView(floorNumber);
    }
}
