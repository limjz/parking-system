package utils;

import java.awt.*;
import javax.swing.JButton;


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
    public static final String DEBT_FILE = "data/outstanding_fines.txt";

    // --- DELIMITERS ---
    public static final String DELIMITER_READ = "\\|"; 
    public static final String DELIMITER_WRITE = "|";


    // button style 
    public static final Color COLOR_PRIMARY = new Color (108, 117, 125); // Grey

    public static final Color COLOR_TEXT_WHITE = Color.WHITE; // white font 

    // font and button size 
    public static final Font FONT_BUTTON = new Font("Arial", Font.BOLD, 14);
    public static final Dimension BTN_SIZE_MINI = new Dimension(75, 25);
    public static final Dimension BTN_SIZE_SMALL = new Dimension(120, 25);
    public static final Dimension BTN_SIZE_STANDARD = new Dimension(175, 25);
    public static final Dimension BTN_SIZE_MEDIUM = new Dimension(200, 30);

    // bnutton template 
    public static void styleButton(JButton btn, Color bgColor, Dimension size) {
        // btn.setFont(FONT_BUTTON);
        // btn.setBackground(bgColor);
        // btn.setForeground(COLOR_TEXT_WHITE);
        // btn.setFocusPainted(false); // Removes the focus border
        btn.setPreferredSize(size);
        btn.setOpaque(true);
        // btn.setBorderPainted(false); // Optional: Flat look
    }


}