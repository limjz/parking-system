package models;

public class TicketRecord {
    private final String ticketId;
    private final String plate;
    private final String entryTime;
    private final String exitTime;
    private final double paymentAmount;
    private final String status;
    private final double fineAmount;
    private final String spot; // optional

    public TicketRecord(String ticketId, String plate, String entryTime, String exitTime,
                        double paymentAmount, String status, double fineAmount, String spot) {
        this.ticketId = ticketId;
        this.plate = plate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.paymentAmount = paymentAmount;
        this.status = status;
        this.fineAmount = fineAmount;
        this.spot = spot;
    }

    public String getTicketId() { return ticketId; }
    public String getPlate() { return plate; }
    public String getEntryTime() { return entryTime; }
    public String getExitTime() { return exitTime; }
    public double getPaymentAmount() { return paymentAmount; }
    public String getStatus() { return status; }
    public double getFineAmount() { return fineAmount; }
    public String getSpot() { return spot; }

    public boolean isParked() {
        return "PARKED".equalsIgnoreCase(status) || "ACTIVE".equalsIgnoreCase(status) || "IN".equalsIgnoreCase(status);
    }

    public boolean isPaidOrExited() {
        return "PAID".equalsIgnoreCase(status) || "EXITED".equalsIgnoreCase(status) || "OUT".equalsIgnoreCase(status);
    }

    public boolean isUnpaidFine() {
        return "UNPAID".equalsIgnoreCase(status) || (fineAmount > 0 && !isPaidOrExited());
    }
}
