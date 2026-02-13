package views;

import controllers.DebtController;
import controllers.TicketController;
import java.awt.*;
import javax.swing.*;
import models.Ticket;
import utils.Config;

public class PaymentPage extends JFrame {

    private TicketController ticketController = new TicketController();
    private DebtController debtController = new DebtController(); 


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
        
        // ------ parking fee ------
        addInfoLabel(mainPanel, String.format("Parking Fees: RM %.2f", ticket.getParkingFeeAmount()));
        mainPanel.add(Box.createVerticalStrut(5));

        // Highlight Fine in RED if it exists
        // ------ fine ------ 
        JLabel lblFine = new JLabel(String.format("Fines: RM %.2f", ticket.getFineAmount()));
        lblFine.setFont(new Font("Arial", Font.PLAIN, 14));
        lblFine.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (ticket.getFineAmount() > 0) {
            lblFine.setForeground(Color.RED);
            lblFine.setText(lblFine.getText() + " (Overstay > 24h)");
        }
        mainPanel.add(lblFine);
        mainPanel.add(Box.createVerticalStrut(10));


        if (ticket.getPreviousDebt() > 0) {
            JLabel lblDebt = new JLabel(String.format("Outstanding Debt: RM %.2f", ticket.getPreviousDebt()));
            lblDebt.setForeground(Color.RED);
            lblDebt.setFont(new Font("Arial", Font.BOLD, 14));
            mainPanel.add(lblDebt);
        }


        // ------ pay total  ------
        JLabel lblTotal = new JLabel(String.format("Total Amount: RM %.2f", ticket.getTotalPayAmount()));
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
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); // Add padding at bottom

        JButton cancelButton = new JButton("Cancel");
        JButton payFeeOnlyButton = new JButton("Pay Fee Only");
        JButton payAllButton = new JButton("Pay Full Amount");

        // button style
        Config.styleButton(cancelButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);
        Config.styleButton(payFeeOnlyButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);
        Config.styleButton(payAllButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);
        
        //no overstay fine and no outstanding debt
        if (ticket.getFineAmount() == 0 && ticket.getPreviousDebt() == 0)
        { 
            payFeeOnlyButton.setEnabled(false);
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
            processPayment(ticket, false);
        });

        payAllButton.addActionListener(e-> { 
            processPayment(ticket, true);
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


    private void processPayment (Ticket ticket, boolean isFullPayment){ 

        // if fullpayment: parking fee + fines + debt 
        // else: only parking fee
        double amountToPay = isFullPayment ? ticket.getTotalPayAmount() : ticket.getParkingFeeAmount(); 

        int choice = JOptionPane.showConfirmDialog(this, 
            "Confirm Payment of RM " + String.format("%.2f", amountToPay) + "?", 
                "Process Payment", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION){ 

            // save exit time to db 
            boolean success = ticketController.completeExit(ticket);
            if (success){ 
                if(isFullPayment){ 
                    // paid everything 
                    if (ticket.getPreviousDebt() > 0){ 
                        debtController.clearDebt(ticket.getPlate());
                    }
                }
                else { 
                    // pay parking fee only
                    double newDebt = ticket.getFineAmount(); 
                    if (newDebt > 0){ 
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