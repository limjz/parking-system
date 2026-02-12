package views;

import controllers.TicketController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import models.Ticket;
import utils.Config;
import utils.FileHandler;

public class ExitPage extends JFrame {

    private JComboBox<Ticket> vehicleCombo;
    private TicketController controller = new TicketController();

    public ExitPage() {
        super("Exit Terminal");
        setSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT); // Standard size
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Parking Exit");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Label
        gbc.gridy = 1; gbc.gridwidth = 1;
        add(new JLabel("Select Vehicle to Exit:"), gbc);

        // Dropdown
        vehicleCombo = new JComboBox<>();
        loadActiveTickets();
        gbc.gridx = 1; 
        add(vehicleCombo, gbc);

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton btnBack = new JButton("Back");
        JButton btnNext = new JButton("Proceed to Payment");
        btnPanel.add(btnBack);
        btnPanel.add(btnNext);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(btnPanel, gbc);

        // --- ACTIONS ---
        btnBack.addActionListener(e -> {
            new EntryExitView().setVisible(true);
            dispose();
        });

        btnNext.addActionListener(e -> {
            Ticket selected = (Ticket) vehicleCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a vehicle.");
                return;
            }

            // 1. Calculate Exit Time & Duration (In Memory Only)
            // We do NOT save to file yet.
            selected.processExit(); 

            // 2. Go to Payment Page
            new PaymentPage(selected).setVisible(true);
            dispose();
        });

        if (vehicleCombo.getItemCount() > 0) vehicleCombo.setSelectedIndex(0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void loadActiveTickets() {
        List<String> lines = FileHandler.readAllLines(Config.TICKET_FILE);
        for (String line : lines) {
            try {
                String[] parts = line.split(Config.DELIMITER_READ);
                if (parts.length < 6) continue;
                String exitTime = (parts.length > 6) ? parts[6] : "null";
                
                // Only load if NOT exited
                if (exitTime.equals("null")) {
                    Ticket t = new Ticket(parts[0], parts[1], Boolean.parseBoolean(parts[2]), 
                                          Boolean.parseBoolean(parts[3]), parts[4], parts[5], null, null, null);
                    vehicleCombo.addItem(t);
                }
            } catch (Exception e) {}
        }
    }
}