package views;  

import controllers.TicketController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import models.SpotType;
import models.VehicleType;
import utils.Config;
import utils.FileHandler;

public class ParkingPage extends JFrame {

    private final String plate;
    //private final String vehicleType;
    private final VehicleType vehicleType;


    private final boolean isHandicappedPerson; // User claimed to be handicapped
    private final boolean hasHandicappedCard;  // User has the card
    
    private final TicketController ticketController = new TicketController();

    private final Color COL_COMPACT = new Color(173, 216, 230); 
    private final Color COL_REGULAR = new Color(144, 238, 144); 
    private final Color COL_HANDICAP = new Color(255, 255, 224); 
    private final Color COL_RESERVED = new Color(255, 182, 193); 
    private final Color COL_OCCUPIED = new Color(169, 169, 169); 

    public ParkingPage(String plate, VehicleType vehicleType, boolean isHandicappedPerson, boolean hasHandicappedCard) {
        this.plate = plate;
        this.vehicleType = vehicleType;
        this.isHandicappedPerson = isHandicappedPerson;
        this.hasHandicappedCard = hasHandicappedCard;

        setTitle("Select Parking Spot");
        setSize(850, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createLegendPanel(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Floor 1", createFloorPanel("F1")); 
        tabbedPane.addTab("Floor 2", createFloorPanel("F2"));
        tabbedPane.addTab("Floor 3", createFloorPanel("F3"));
        tabbedPane.addTab("Floor 4", createFloorPanel("F4"));
        tabbedPane.addTab("Floor 5", createFloorPanel("F5"));
        add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Back to Entry");
        // backButton.setPreferredSize(new Dimension(150, 30));
        Config.styleButton(backButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);
        backButton.addActionListener(e -> {
            new EntryPage().setVisible(true);
            dispose();
        });
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Spot Types & Hourly Rates"));
        panel.setBackground(Color.WHITE);

        panel.add(createLegendItem("Handicapped (RM 2/hr)", COL_HANDICAP));
        panel.add(createLegendItem("Reserved (RM 10/hr)", COL_RESERVED));
        panel.add(createLegendItem("Compact (RM 2/hr)", COL_COMPACT));
        panel.add(createLegendItem("Regular (RM 5/hr)", COL_REGULAR));
        panel.add(createLegendItem("Occupied", COL_OCCUPIED));
        return panel;
    }

    private JLabel createLegendItem(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(color);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        label.setPreferredSize(new Dimension(160, 25)); 
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JPanel createFloorPanel(String floorSuffix) {
        JPanel floorPanel = new JPanel(new GridLayout(4, 5, 10, 10));
        floorPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String filename = Config.PARKINGSPOT_BASE_FILE + floorSuffix + ".txt";
        List<String> spots = FileHandler.readAllLines(filename);

        if (spots.isEmpty()) {
            floorPanel.add(new JLabel("Error: " + filename + " not found."));
            return floorPanel;
        }

        for (String line : spots) {
            try {
                String[] parts = line.split(Config.DELIMITER_READ);
                if (parts.length < 3) continue;

                String spotID = parts[0];     
                SpotType spotType = SpotType.fromString(parts[1]);       
                boolean isOccupied = Boolean.parseBoolean(parts[2]);
                String currentPlate = (parts.length > 3) ? parts[3] : "null";
                boolean spotHasHandicap = (parts.length > 4) && Boolean.parseBoolean(parts[4]);

                JButton spotButton = new JButton("<html><center>" + spotID + "<br/>(" + spotType + ")</center></html>");
                
                switch (spotType) {
                    case COMPACT -> spotButton.setBackground(COL_COMPACT);
                    case REGULAR -> spotButton.setBackground(COL_REGULAR);
                    case HANDICAPPED -> spotButton.setBackground(COL_HANDICAP);
                    case RESERVED -> spotButton.setBackground(COL_RESERVED);
                    default ->  spotButton.setBackground(Color.LIGHT_GRAY);
                }

                if (isOccupied) {
                    spotButton.setBackground(COL_OCCUPIED);
                    String labelText = "<html><center>" + spotID + "<br/>[OCCUPIED]<br/><b>" + currentPlate + "</b>";
                    if (spotHasHandicap) labelText += "<br/><font color='blue'>(HC)</font>";
                    labelText += "</center></html>";
                    spotButton.setText(labelText);
                    spotButton.setEnabled(false); 
                } else {
                    spotButton.addActionListener(e -> {

                        // ------ Spot restriction logic ------

                        // if the vehicle is handicapped, then it can park at any spot
                        if (this.vehicleType == VehicleType.HANDICAPPED) {
                            // Proceed immediately (Skip size & reserved checks)
                        } 
                        else {
                            // --- Logic for NON-Handicapped Vehicles ---

                            // Non-Handicapped people cannot use Handicapped spots
                            if (spotType == SpotType.HANDICAPPED) {
                                JOptionPane.showMessageDialog(this, 
                                    "Only Handicapped vehicles can park here.", 
                                    "Invalid Selection", 
                                    JOptionPane.WARNING_MESSAGE);
                                return;
                            }

                            // check for non-special spot if the car fits the spot 
                            boolean sizeFit = false;

                            switch (this.vehicleType) {
                                // Motorcycle: Compact ONLY
                                case MOTORCYCLE -> {
                                    if (spotType == SpotType.COMPACT) sizeFit = true;
                                }

                                // Car: Compact or Regular
                                case CAR -> {
                                    if (spotType == SpotType.COMPACT || spotType == SpotType.REGULAR) sizeFit = true;
                                }

                                // SUV: Regular ONLY
                                case SUV -> {
                                    if (spotType == SpotType.REGULAR) sizeFit = true;
                                }
                            }

                            if (spotType == SpotType.RESERVED) {
                                sizeFit = true; 
                            }

                            if (!sizeFit) {
                                JOptionPane.showMessageDialog(this, 
                                    "Vehicle Mismatch: " + vehicleType + " cannot fit in " + spotType, 
                                    "Invalid Selection", 
                                    JOptionPane.WARNING_MESSAGE);
                                return;
                            }

                            // Restriction 2: Reserved Spot (Soft Block / Fine Warning)
                            if (spotType == SpotType.RESERVED) {
                                boolean isVip = ticketController.isVip(this.plate);
                                if (!isVip) {
                                    int confirm = JOptionPane.showConfirmDialog(this, 
                                        "WARNING: This is a RESERVED spot and you are not a VIP.\n" +
                                        "You will be fined according to the scheme.\n\nContinue?", 
                                        "Violation Warning", JOptionPane.YES_NO_OPTION);
                                    
                                    if (confirm == JOptionPane.NO_OPTION) return; // User backed out
                                }
                            }
                        }

                        // --- CONFIRMATION ---
                        int choice = JOptionPane.showConfirmDialog(this, "Select spot " + spotID + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            new ConfirmationPage(plate, vehicleType, isHandicappedPerson, hasHandicappedCard, spotID).setVisible(true);
                            dispose();
                        }
                    });
                }
                floorPanel.add(spotButton);

            } catch (Exception e) {
                System.out.println("Error processing line: " + line);
            }
        }
        return floorPanel;
    }
}