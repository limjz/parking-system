package views;  

import controllers.TicketController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import utils.Config;
import utils.FileHandler;

public class ParkingPage extends JFrame {

    private String plate;
    private String vehicleType;
    private boolean isHandicappedPerson; // User claimed to be handicapped
    private boolean hasHandicappedCard;  // User has the card
    
    private TicketController controller = new TicketController();

    private final Color COL_COMPACT = new Color(173, 216, 230); 
    private final Color COL_REGULAR = new Color(144, 238, 144); 
    private final Color COL_HANDICAP = new Color(255, 255, 224); 
    private final Color COL_RESERVED = new Color(255, 182, 193); 
    private final Color COL_OCCUPIED = new Color(169, 169, 169); 

    public ParkingPage(String plate, String vehicleType, boolean isHandicappedPerson, boolean hasHandicappedCard) {
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
        JButton btnBack = new JButton("Back to Entry");
        btnBack.setPreferredSize(new Dimension(150, 30));
        btnBack.addActionListener(e -> {
            new EntryPage().setVisible(true);
            dispose();
        });
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Spot Types & Hourly Rates"));
        panel.setBackground(Color.WHITE);
        panel.add(createLegendItem("Compact (RM 2/hr)", COL_COMPACT));
        panel.add(createLegendItem("Regular (RM 5/hr)", COL_REGULAR));
        panel.add(createLegendItem("Handicapped (RM 2/hr)", COL_HANDICAP));
        panel.add(createLegendItem("Reserved (RM 10/hr)", COL_RESERVED));
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
                String spotType = parts[1];       
                boolean isOccupied = Boolean.parseBoolean(parts[2]);
                String currentPlate = (parts.length > 3) ? parts[3] : "null";
                boolean spotHasHandicap = (parts.length > 4) && Boolean.parseBoolean(parts[4]);

                JButton spotButton = new JButton("<html><center>" + spotID + "<br/>(" + spotType + ")</center></html>");
                
                switch (spotType) {
                    case "Compact": spotButton.setBackground(COL_COMPACT); break;
                    case "Regular": spotButton.setBackground(COL_REGULAR); break;
                    case "Handicapped": spotButton.setBackground(COL_HANDICAP); break;
                    case "Reserved": spotButton.setBackground(COL_RESERVED); break;
                    default: spotButton.setBackground(Color.LIGHT_GRAY);
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
                        
                        // --- RESTRICTION LOGIC ---
                        
                        // 1. If User Is Handicapped -> MUST use Handicapped Spot
                        if (this.isHandicappedPerson) {
                            if (!spotType.equalsIgnoreCase("Handicapped")) {
                                JOptionPane.showMessageDialog(this, 
                                    "Restriction: Handicapped vehicles can ONLY park in Handicapped spots.", 
                                    "Invalid Selection", 
                                    JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        } 
                        // 2. If User is NOT Handicapped -> CANNOT use Handicapped Spot
                        else {
                            if (spotType.equalsIgnoreCase("Handicapped")) {
                                JOptionPane.showMessageDialog(this, 
                                    "Restriction: Only Handicapped vehicles can park here.", 
                                    "Invalid Selection", 
                                    JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }

                        // 3. Size Compatibility (Only applies if they are not forced into HC spots)
                        if (!this.isHandicappedPerson && !spotType.equalsIgnoreCase("Reserved")) {
                            boolean sizeFit = false;
                            switch (this.vehicleType) {
                                case "Motorcycle": if (spotType.equalsIgnoreCase("Compact")) sizeFit = true; break;
                                case "Car": if (spotType.equalsIgnoreCase("Compact") || spotType.equalsIgnoreCase("Regular")) sizeFit = true; break;
                                case "SUV/Truck": if (spotType.equalsIgnoreCase("Regular")) sizeFit = true; break;
                            }
                            if (!sizeFit) {
                                JOptionPane.showMessageDialog(this, "Vehicle Mismatch: " + vehicleType + " cannot fit in " + spotType, "Invalid Selection", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }

                        // 4. VIP Check
                        if (spotType.equalsIgnoreCase("Reserved")) {
                            if (!controller.isVip(this.plate)) {
                                JOptionPane.showMessageDialog(this, "Access Denied: Not a VIP.", "Restricted Access", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }

                        // --- CONFIRMATION ---
                        int choice = JOptionPane.showConfirmDialog(this, "Select spot " + spotID + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            // Pass both booleans to confirmation
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