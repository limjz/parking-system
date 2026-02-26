package views;

import controllers.TicketController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.Ticket;
import utils.Config;

public class TicketView extends JFrame {

    private JComboBox<Ticket> vehicleCombo;
    private JLabel lblPlate = new JLabel("-");
    private JLabel lblSpot = new JLabel("-");
    private JLabel lblType = new JLabel("-");
    private JLabel lblIsHandicap = new JLabel("-");
    private JLabel lblHasCard = new JLabel("-");
    private JLabel lblEntry = new JLabel("-");

    public TicketView() {
        setTitle("View Active Tickets");
        setSize(Config.WINDOW_WIDTH, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TOP ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel titleLabel = new JLabel("Active Tickets", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectorPanel.add(new JLabel("Select Vehicle:"));

        vehicleCombo = new JComboBox<>();
        loadTickets(); 
        
        vehicleCombo.addActionListener(e -> displayTicketDetails());
        vehicleCombo.setPreferredSize(new Dimension(250, 30));
        selectorPanel.add(vehicleCombo);
        headerPanel.add(selectorPanel);

        add(headerPanel, BorderLayout.NORTH);

        // ------------ CENTER ------------
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Ticket Details"));
        
        infoPanel.add(new JLabel("License Plate:")); infoPanel.add(lblPlate);
        infoPanel.add(new JLabel("Parking Spot:")); infoPanel.add(lblSpot);
        infoPanel.add(new JLabel("Vehicle Type:")); infoPanel.add(lblType);
        infoPanel.add(new JLabel("Is Handicapped:")); infoPanel.add(lblIsHandicap);
        infoPanel.add(new JLabel("Has Card:")); infoPanel.add(lblHasCard);
        infoPanel.add(new JLabel("Entry Time:")); infoPanel.add(lblEntry);

        // Styles
        Font font = new Font("Arial", Font.BOLD, 14);
        lblPlate.setForeground(Color.BLUE); lblPlate.setFont(font);
        lblSpot.setForeground(Color.RED);   lblSpot.setFont(font);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        centerPanel.add(infoPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // ------------ BOTTOM ------------
        JPanel btnPanel = new JPanel();
        JButton btnBack = new JButton("Back to Main Menu");

        Config.styleButton(btnBack, Config.COLOR_PRIMARY, Config.BTN_SIZE_MEDIUM);

        //  ------------ Action Listener ------------
        btnBack.addActionListener(e -> {
            new EntryExitView().setVisible(true);
            dispose();
        });
        btnPanel.add(btnBack);
        add(btnPanel, BorderLayout.SOUTH);
        
        if (vehicleCombo.getItemCount() > 0) vehicleCombo.setSelectedIndex(0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    private void displayTicketDetails() {
        Ticket selected = (Ticket) vehicleCombo.getSelectedItem();
        if (selected != null) {
            lblPlate.setText(selected.getPlate());
            lblSpot.setText(selected.getSpotID());
            lblType.setText(selected.getVehicleType());
            
            lblIsHandicap.setText(selected.isHandicappedPerson() ? "YES" : "NO");
            lblIsHandicap.setForeground(selected.isHandicappedPerson() ? Color.RED : Color.BLACK);

            lblHasCard.setText(selected.hasCard() ? "YES" : "NO");
            lblHasCard.setForeground(selected.hasCard() ? new Color(0, 100, 0) : Color.BLACK);

            lblEntry.setText(selected.getEntryTimeStr());
        }
    }


    private void loadTickets () { 
        TicketController ticketController = TicketController.getInstance();
        List<Ticket> allTickets = ticketController.getAllTickets(); 

        for (Ticket t : allTickets)
        { 
            vehicleCombo.addItem(t);
        }
    }
}