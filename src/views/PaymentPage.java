package views;

import controllers.TicketController;
import java.awt.*;
import javax.swing.*;
import models.Ticket;
import utils.Config;

public class PaymentPage extends JFrame {

    private TicketController controller = new TicketController();

    public PaymentPage(Ticket ticket) {
        setTitle("Payment Counter");
        setSize(Config.WINDOW_WIDTH, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TITLE ---
        JLabel title = new JLabel("Payment Details", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // --- CENTER: INFO & OPTIONS ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // 1. Vehicle Info (Read directly from the Ticket Object)
        addInfoLabel(mainPanel, "License Plate: " + ticket.getPlate());
        addInfoLabel(mainPanel, "Entry Time: " + ticket.getEntryTimeStr()); 
        addInfoLabel(mainPanel, "Exit Time: " + ticket.getExitTimeStr());
        addInfoLabel(mainPanel, "Duration: " + ticket.getDurationStr());
        
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 2. Fees & Fines (Formatted to 2 decimal places)
        addInfoLabel(mainPanel, String.format("Parking Fees: RM %.2f", ticket.getParkingFeeAmount()));
        mainPanel.add(Box.createVerticalStrut(5));

        // Highlight Fine in RED if it exists
        JLabel lblFine = new JLabel(String.format("Fines: RM %.2f", ticket.getFineAmount()));
        lblFine.setFont(new Font("Arial", Font.PLAIN, 14));
        lblFine.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (ticket.getFineAmount() > 0) {
            lblFine.setForeground(Color.RED);
            lblFine.setText(lblFine.getText() + " (Overstay > 24h)");
        }
        mainPanel.add(lblFine);
        mainPanel.add(Box.createVerticalStrut(10));

        // Total
        JLabel lblTotal = new JLabel(String.format("Total Amount: RM %.2f", ticket.getPayAmount()));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(0, 100, 0)); // Dark Green
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblTotal);
        mainPanel.add(Box.createVerticalStrut(20));

        // 3. Payment Options
        JLabel lblPay = new JLabel("Select Payment Method:");
        lblPay.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblPay);
        
        JRadioButton rCard = new JRadioButton("Credit/Debit Card");
        JRadioButton rWallet = new JRadioButton("E-Wallet");
        JRadioButton rQR = new JRadioButton("QR Pay");
        
        ButtonGroup group = new ButtonGroup();
        group.add(rCard); group.add(rWallet); group.add(rQR);
        rCard.setSelected(true); 

        mainPanel.add(rCard);
        mainPanel.add(rWallet);
        mainPanel.add(rQR);

        add(mainPanel, BorderLayout.CENTER);

        // --- BOTTOM: BUTTONS ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnCancel = new JButton("Cancel");
        JButton btnPay = new JButton("Confirm Payment & Exit");

        btnPanel.add(btnCancel);
        btnPanel.add(btnPay);
        add(btnPanel, BorderLayout.SOUTH);

        // --- ACTIONS ---
        btnCancel.addActionListener(e -> {
            // Logic: If they cancel, we DO NOT save the exit time.
            // We go back, and the car stays "Parked".
            new ExitPage().setVisible(true);
            dispose();
        });

        btnPay.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, 
                "Confirm Payment of RM " + String.format("%.2f", ticket.getPayAmount()) + "?", 
                "Process Payment", JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                // NOW we save to the database!
                boolean success = controller.completeExit(ticket);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Payment Successful!\nReceipt Generated.\nGate Opening...");
                    new EntryExitView().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error processing exit (File Write Error).");
                }
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void addInfoLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
    }
}