package views;

import controllers.TicketController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import models.Ticket;
import utils.Config;

public class ExitPage extends JFrame {

    private JComboBox<Ticket> vehicleCombo;
    private TicketController ticketController = new TicketController();

    public ExitPage() {
        super("Exit Terminal");
        setSize(Config.WINDOW_WIDTH/2, Config.WINDOW_HEIGHT/2); // Standard size
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
        List<Ticket> allTickets = ticketController.getAllTickets();
        for (Ticket t : allTickets)
        { 
            // Check if exit time is "-" or "null"
            if (t.getExitTimeStr().equals("-") || t.getExitTimeStr().equals("null")) {
                vehicleCombo.addItem(t);
            }        
        }
        gbc.gridx = 1; 
        add(vehicleCombo, gbc);

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton backButton = new JButton("Back");
        JButton nexButton = new JButton("Proceed to Payment");

        Config.styleButton(backButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_SMALL);
        Config.styleButton(nexButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_SMALL);
        
        btnPanel.add(backButton);
        btnPanel.add(nexButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(btnPanel, gbc);

        // --- ACTIONS ---
        backButton.addActionListener(e -> {
            new EntryExitView().setVisible(true);
            dispose();
        });

        nexButton.addActionListener(e -> {
            Ticket selected = (Ticket) vehicleCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a vehicle.");
                return;
            }
            // calc the fines and payment 
            ticketController.processTicketExit(selected);

            // Go to Payment Page
            new PaymentPage(selected).setVisible(true);
            dispose();
        });

        if (vehicleCombo.getItemCount() > 0) {
            vehicleCombo.setSelectedIndex(0);
        };

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}