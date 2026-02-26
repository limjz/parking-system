package views;

import controllers.TicketController;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import models.VehicleType;
import utils.Config;

public class ConfirmationPage extends JFrame {

    private final TicketController ticketController = TicketController.getInstance();

    // Constructor updated to take both booleans
    public ConfirmationPage(String plate, VehicleType type, boolean isHandicappedPerson, boolean hasCard, String spotID) {
        setTitle("Confirm Ticket Details");
        setSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ------------ TITLE ------------
        JLabel titleLabel = new JLabel("Please Verify Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // ------------ DETAILS PANEL ------------
        JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 10, 10)); // Increased rows
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        detailsPanel.add(new JLabel("License Plate:"));
        detailsPanel.add(new JLabel(plate));

        detailsPanel.add(new JLabel("Vehicle Type:"));
        detailsPanel.add(new JLabel(type.toString()));

        detailsPanel.add(new JLabel("Is Handicapped:"));
        detailsPanel.add(new JLabel(isHandicappedPerson ? "Yes" : "No"));

        detailsPanel.add(new JLabel("Has Card:"));
        detailsPanel.add(new JLabel(hasCard ? "Yes" : "No"));

        detailsPanel.add(new JLabel("Assigned Spot:"));
        JLabel spotLabel = new JLabel(spotID);
        spotLabel.setForeground(Color.BLUE);
        spotLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(spotLabel);

        detailsPanel.add(new JLabel("Entry Time:"));
        detailsPanel.add(new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

        add(detailsPanel, BorderLayout.CENTER);

        // ------------ buttons ------------
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton cancelButton = new JButton("Cancel");
        JButton confirmButton = new JButton("Confirm & Generate Ticket");

        btnPanel.add(cancelButton);
        btnPanel.add(confirmButton);
        add(btnPanel, BorderLayout.SOUTH);

        // ------------ Action Listener ------------
        cancelButton.addActionListener(e -> {
            // Return to parking page with correct state
            new ParkingPage(plate, type, isHandicappedPerson, hasCard).setVisible(true);
            dispose();
        });

        confirmButton.addActionListener(e -> {
            // Save using hasCard status
            boolean success = ticketController.generateTicket(plate, type.toString(), isHandicappedPerson, hasCard, spotID);

            if (success) {
                JOptionPane.showMessageDialog(this, "Ticket Generated Successfully!\nPlate: " + plate + "\nSpot: " + spotID);
                new EntryExitView().setVisible(true); 
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not save ticket.");
            }
        });
    }
}