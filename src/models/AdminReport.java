package models;

public class AdminReport {
    // Defines the specific reports available in the system
    public enum Type {
        OCCUPANCY_RATE,
        PARKING_STRUCTURE,
        REVENUE,
        CURRENT_VEHICLES,
        UNPAID_FINES
    }
}