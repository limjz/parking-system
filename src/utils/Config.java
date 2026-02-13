package utils;

public class Config {

    public static final int WINDOW_WIDTH = 900; 
    public static final int WINDOW_HEIGHT = 650; 


    public final static String adminUser = "admin";
    public final static String adminPass = "1234";


    // --- FILE PATHS---
    // This will build paths like "data/parkingspot_F1.txt"
    public static final String PARKINGSPOT_BASE_FILE = "data/parkingspot_"; 
    
    public static final String TICKET_FILE = "data/tickets.txt";
    public static final String VIP_FILE = "data/VIP_Plate.txt";
    public static final String FINE_SCHEME_FILE = "data/fine_scheme.txt"; 

    // --- DELIMITERS ---
    public static final String DELIMITER_READ = "\\|"; 
    public static final String DELIMITER_WRITE = "|";
}