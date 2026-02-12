package views;

import controllers.TicketController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
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
    private JLabel lblExit = new JLabel("-");
    private JLabel lblDuration = new JLabel("-");

    public TicketView() {
        setTitle("View Active Tickets");
        setSize(Config.WINDOW_WIDTH, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TOP ---
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Vehicle:"));
        
        vehicleCombo = new JComboBox<>();
        loadTickets(); 
        
        vehicleCombo.addActionListener(e -> displayTicketDetails());
        vehicleCombo.setPreferredSize(new Dimension(250, 30));
        topPanel.add(vehicleCombo);
        add(topPanel, BorderLayout.NORTH);

        // --- CENTER ---
        JPanel infoPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Ticket Details"));
        
        infoPanel.add(new JLabel("License Plate:")); infoPanel.add(lblPlate);
        infoPanel.add(new JLabel("Parking Spot:")); infoPanel.add(lblSpot);
        infoPanel.add(new JLabel("Vehicle Type:")); infoPanel.add(lblType);
        infoPanel.add(new JLabel("Is Handicapped:")); infoPanel.add(lblIsHandicap);
        infoPanel.add(new JLabel("Has Card:")); infoPanel.add(lblHasCard);
        infoPanel.add(new JLabel("Entry Time:")); infoPanel.add(lblEntry);
        infoPanel.add(new JLabel("Exit Time:")); infoPanel.add(lblExit);
        infoPanel.add(new JLabel("Duration:")); infoPanel.add(lblDuration);

        // Styles
        Font font = new Font("Arial", Font.BOLD, 14);
        lblPlate.setForeground(Color.BLUE); lblPlate.setFont(font);
        lblSpot.setForeground(Color.RED);   lblSpot.setFont(font);

        add(infoPanel, BorderLayout.CENTER);

        // --- BOTTOM ---
        JPanel btnPanel = new JPanel();
        JButton btnBack = new JButton("Back to Main Menu");
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
            lblExit.setText(selected.getExitTimeStr());
            lblDuration.setText(selected.getDurationStr());
        }
    }


    private void loadTickets () { 
        TicketController tc = new TicketController(); 
        List<Ticket> allTickets = tc.getAllTickets(); 

        for (Ticket t : allTickets)
        { 
            vehicleCombo.addItem(t);
        }
    }
}