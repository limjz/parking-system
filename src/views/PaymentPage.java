package views;

import controllers.TicketController;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.Ticket;
import utils.Config;

public class PaymentPage extends JFrame {

    private TicketController controller = new TicketController();

    public PaymentPage(Ticket ticket) {
        setTitle("Payment Counter");
        setSize(Config.WINDOW_WIDTH, 700); // Increased height for details
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TITLE ---
        JLabel title = new JLabel("Payment Receipt", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // --- CENTER: INFO & OPTIONS ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 40, 20, 40));

        // 1. Details Panel (GridBagLayout for alignment)
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 0, 4, 15); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // --- Calculate Rate ---
        double billableHours = Math.ceil(ticket.getHourParked());
        if (billableHours == 0) billableHours = 1.0;
        double ratePerHour = (ticket.getParkingFeeAmount() > 0) ? (ticket.getParkingFeeAmount() / billableHours) : 0.0;

        // Row 0
        addDetailRow(detailsPanel, gbc, 0, "License Plate:", ticket.getPlate());
        addDetailRow(detailsPanel, gbc, 1, "Vehicle Type:", ticket.getVehicleType());
        addDetailRow(detailsPanel, gbc, 2, "Spot ID:", ticket.getSpotID());
        
        // Separator
        gbc.gridy = 3; gbc.gridwidth = 2;
        detailsPanel.add(Box.createVerticalStrut(10), gbc);
        gbc.gridwidth = 1;

        // Row 4-6 (Times)
        addDetailRow(detailsPanel, gbc, 4, "Entry Time:", ticket.getEntryTimeStr());
        addDetailRow(detailsPanel, gbc, 5, "Exit Time:", ticket.getExitTimeStr());
        addDetailRow(detailsPanel, gbc, 6, "Total Duration:", ticket.getDurationStr());

        // Separator
        gbc.gridy = 7; gbc.gridwidth = 2;
        detailsPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Row 8-9 (Calculation)
        addDetailRow(detailsPanel, gbc, 8, "Billable Hours:", String.format("%.0f hrs", billableHours));
        addDetailRow(detailsPanel, gbc, 9, "Hourly Rate:", String.format("RM %.2f /hr", ratePerHour));

        // Row 10: Parking Fees
        addDetailRow(detailsPanel, gbc, 10, "Parking Fees:", String.format("RM %.2f", ticket.getParkingFeeAmount()));

        // Row 11: Fines (Red if exists)
        gbc.gridy = 11; gbc.gridx = 0;
        detailsPanel.add(new JLabel("Fines (Overstay):"), gbc);
        
        gbc.gridx = 1;
        JLabel lblFine = new JLabel(String.format("RM %.2f", ticket.getFineAmount()));
        if (ticket.getFineAmount() > 0) {
            lblFine.setForeground(Color.RED);
            lblFine.setFont(new Font("Arial", Font.BOLD, 12));
        }
        detailsPanel.add(lblFine, gbc);

        // Row 12: Remaining Balance (As requested)
        gbc.gridy = 12; gbc.gridx = 0;
        detailsPanel.add(new JLabel("Remaining Balance:"), gbc);
        gbc.gridx = 1;
        JLabel lblBalance = new JLabel("RM 0.00"); // Placeholder / Default
        lblBalance.setForeground(Color.BLUE);
        detailsPanel.add(lblBalance, gbc);

        mainPanel.add(detailsPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // --- TOTAL ---
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalPanel.setBackground(new Color(240, 240, 240));
        totalPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel lblTotal = new JLabel(String.format("TOTAL TO PAY:  RM %.2f", ticket.getPayAmount()));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(0, 100, 0)); // Dark Green
        totalPanel.add(lblTotal);
        
        mainPanel.add(totalPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // --- PAYMENT OPTIONS ---
        JLabel lblPay = new JLabel("Select Payment Method:");
        lblPay.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPay.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblPay);
        mainPanel.add(Box.createVerticalStrut(5));
        
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JRadioButton rCard = new JRadioButton("Credit/Debit Card");
        JRadioButton rWallet = new JRadioButton("E-Wallet");
        JRadioButton rQR = new JRadioButton("QR Pay");
        
        ButtonGroup group = new ButtonGroup();
        group.add(rCard); group.add(rWallet); group.add(rQR);
        rCard.setSelected(true); 

        radioPanel.add(rCard);
        radioPanel.add(rWallet);
        radioPanel.add(rQR);
        mainPanel.add(radioPanel);

        add(mainPanel, BorderLayout.CENTER);

        // --- BOTTOM: BUTTONS ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        JButton btnCancel = new JButton("Cancel");
        JButton btnPay = new JButton("Confirm Payment & Exit");
        btnPay.setPreferredSize(new Dimension(200, 30));
        btnPay.setForeground(Color.BLACK);

        btnPanel.add(btnCancel);
        btnPanel.add(btnPay);
        add(btnPanel, BorderLayout.SOUTH);

        // --- ACTIONS ---
        btnCancel.addActionListener(e -> {
            new ExitPage().setVisible(true);
            dispose();
        });

        btnPay.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, 
                "Confirm Payment of RM " + String.format("%.2f", ticket.getPayAmount()) + "?", 
                "Process Payment", JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = controller.completeExit(ticket);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Payment Successful!\nReceipt Generated.\nGate Opening...");
                    new EntryExitView().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error processing exit (File Write Error).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(lblValue, gbc);
    }
}