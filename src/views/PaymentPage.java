package views;

import controllers.DebtController;
import controllers.TicketController;
import controllers.TransactionController;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.Ticket;
import utils.Config;

public class PaymentPage extends JFrame {

    private final TicketController ticketController = new TicketController();
    private final DebtController debtController = new DebtController(); 
    private final TransactionController transactionController = new TransactionController();
    
    private JRadioButton rCard, rWallet, rQR, rCash;


    public PaymentPage(Ticket ticket) {
        setTitle("Payment Counter");
        setSize(Config.WINDOW_WIDTH, 700); 
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

        //  Details Panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 0, 4, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // --- Calculate Rate ---
        double billableHours = Math.ceil(ticket.getHourParked());
        if (billableHours == 0) billableHours = 1.0;
        double ratePerHour = (ticket.getParkingFeeAmount() > 0) ? (ticket.getParkingFeeAmount() / billableHours) : 0.0;

        // Rows
        addDetailRow(detailsPanel, gbc, 0, "License Plate:", ticket.getPlate());
        addDetailRow(detailsPanel, gbc, 1, "Vehicle Type:", ticket.getVehicleType());
        addDetailRow(detailsPanel, gbc, 2, "Spot ID:", ticket.getSpotID());
        
        gbc.gridy = 3; gbc.gridwidth = 2;
        detailsPanel.add(Box.createVerticalStrut(10), gbc);
        gbc.gridwidth = 1;

        addDetailRow(detailsPanel, gbc, 4, "Entry Time:", ticket.getEntryTimeStr());
        addDetailRow(detailsPanel, gbc, 5, "Exit Time:", ticket.getExitTimeStr());
        addDetailRow(detailsPanel, gbc, 6, "Total Duration:", ticket.getDurationStr());

        gbc.gridy = 7; gbc.gridwidth = 2;
        detailsPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        addDetailRow(detailsPanel, gbc, 8, "Billable Hours:", String.format("%.0f hrs", billableHours));
        addDetailRow(detailsPanel, gbc, 9, "Hourly Rate:", String.format("RM %.2f /hr", ratePerHour));
        addDetailRow(detailsPanel, gbc, 10, "Parking Fees:", String.format("RM %.2f", ticket.getParkingFeeAmount()));

        // Fines (Red if exists)
        gbc.gridy = 11; gbc.gridx = 0;
        detailsPanel.add(new JLabel("Fines:"), gbc);
        gbc.gridx = 1;
        JLabel lblFine = new JLabel(String.format("RM %.2f", ticket.getFineAmount()));
        if (ticket.getFineAmount() > 0) {
            lblFine.setForeground(Color.RED);
            lblFine.setFont(new Font("Arial", Font.BOLD, 12));
        }
        detailsPanel.add(lblFine, gbc);

        // Previous Debt (Red if exists)
        gbc.gridy = 12; gbc.gridx = 0;
        detailsPanel.add(new JLabel("Outstanding Debt:"), gbc);
        gbc.gridx = 1;
        JLabel lblDebt = new JLabel(String.format("RM %.2f", ticket.getPreviousDebt()));
        if (ticket.getPreviousDebt() > 0) {
            lblDebt.setForeground(Color.RED);
            lblDebt.setFont(new Font("Arial", Font.BOLD, 12));
        }
        detailsPanel.add(lblDebt, gbc);

        mainPanel.add(detailsPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // --- TOTAL ---
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalPanel.setBackground(new Color(240, 240, 240));
        totalPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel lblTotal = new JLabel(String.format("TOTAL TO PAY:  RM %.2f", ticket.getTotalPayAmount()));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(0, 100, 0)); 
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
        
        rCard = new JRadioButton("Credit/Debit Card");
        rWallet = new JRadioButton("E-Wallet");
        rQR = new JRadioButton("QR Pay");
        rCash = new JRadioButton("Cash");
        
        ButtonGroup group = new ButtonGroup();
        group.add(rCard); 
        group.add(rWallet); 
        group.add(rQR);
        group.add(rCash);
        rCard.setSelected(true); 

        radioPanel.add(rCard); 
        radioPanel.add(rWallet); 
        radioPanel.add(rQR);
        radioPanel.add(rCash);

        mainPanel.add(radioPanel);

        add(mainPanel, BorderLayout.CENTER);

        // --- BOTTOM: BUTTONS  ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); 

        JButton cancelButton = new JButton("Cancel");
        JButton payFeeOnlyButton = new JButton("Pay Fee Only");
        JButton payAllButton = new JButton("Pay Full Amount");

        // Style Buttons 
        Config.styleButton(cancelButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);
        Config.styleButton(payFeeOnlyButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD); // Orange for deferring
        Config.styleButton(payAllButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);

        // check if no oustanding fines and debt, pay all is just pay parking fee
        if (ticket.getFineAmount() == 0 && ticket.getPreviousDebt() == 0) {
            payFeeOnlyButton.setEnabled(false);
            payFeeOnlyButton.setBackground(Color.LIGHT_GRAY);
        }

        btnPanel.add(cancelButton);
        btnPanel.add(payFeeOnlyButton);
        btnPanel.add(payAllButton);
        add(btnPanel, BorderLayout.SOUTH);

        // --- ACTIONS ---
        cancelButton.addActionListener(e -> {
            new ExitPage().setVisible(true);
            dispose();
        });

        payFeeOnlyButton.addActionListener(e -> {
            String method = getSelectedMethod ();
            processPayment(ticket, false, method);
        });

        payAllButton.addActionListener(e -> { 
            String method = getSelectedMethod ();
            processPayment(ticket, true, method);
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // Helper for grid rows
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


    private String getSelectedMethod (){ 
        if (rCard.isSelected()) return "CARD";
        if (rWallet.isSelected()) return "E-WALLET";
        if (rQR.isSelected()) return "QR PAY";
        if (rCash.isSelected()) return "CASH";
        return "UNKNOWN";
    }

    private void processPayment(Ticket ticket, boolean isFullPayment, String paymentMethod) { 
        // Logic: Pay Full (Total) OR Pay Fee Only (Fee)
        double amountToPay = isFullPayment ? ticket.getTotalPayAmount() : ticket.getParkingFeeAmount(); 
        String note = isFullPayment ? "FULL_PAYMENT" : "FEE_ONLY_DEFER_FINE";

        int choice = JOptionPane.showConfirmDialog(this, 
            "Confirm Payment of RM " + String.format("%.2f", amountToPay) + "?", 
            "Process Payment", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) { 

            ticket.setAmountPaid(amountToPay);
            // save exit time to db
            boolean success = ticketController.completeExit(ticket);
            

            if (success) { 

                transactionController.logTransaction(ticket.getPlate(), amountToPay, paymentMethod, note);

                if (isFullPayment) { 
                    // pay everything 
                    if (ticket.getPreviousDebt() > 0) { 
                        debtController.clearDebt(ticket.getPlate());
                    }
                } else { 
                    // OPTION B: Paid Fee Only -> Add Current Fine to Debt
                    // (Old debt stays in file, New fine is appended)
                    double newDebt = ticket.getFineAmount(); 
                    if (newDebt > 0) { 
                        debtController.addDebt(ticket.getPlate(), newDebt);
                    } 
                }
                JOptionPane.showMessageDialog(this, "Payment Successful!\n");
                new EntryExitView().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error writing to file.");
            }
        }
    }
}