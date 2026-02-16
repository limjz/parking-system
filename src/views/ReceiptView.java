package views;

import controllers.TicketController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.Ticket;
import utils.Config;

public class ReceiptView extends JFrame {

    private JComboBox<Ticket> receiptCombo;
    private JLabel lblPlate = new JLabel("-");
    private JLabel lblSpot = new JLabel("-");
    private JLabel lblType = new JLabel("-");
    private JLabel lblEntry = new JLabel("-");
    private JLabel lblExit = new JLabel("-");
    private JLabel lblBillableHours = new JLabel("-");
    private JLabel lblFee = new JLabel("-");
    private JLabel lblFine = new JLabel("-");
    private JLabel lblDebt = new JLabel("-");
    private JLabel lblTotal = new JLabel("-");
    private JLabel lblBalance = new JLabel("-");

    public ReceiptView() {
        setTitle("View Receipts");
        setSize(Config.WINDOW_WIDTH, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel titleLabel = new JLabel("Receipts", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selectorPanel.add(new JLabel("Select Receipt:"));

        receiptCombo = new JComboBox<>();
        loadReceipts();

        receiptCombo.addActionListener(e -> displayReceiptDetails());
        receiptCombo.setPreferredSize(new Dimension(250, 30));
        selectorPanel.add(receiptCombo);
        headerPanel.add(selectorPanel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(11, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Receipt Details"));

        infoPanel.add(new JLabel("License Plate:")); infoPanel.add(lblPlate);
        infoPanel.add(new JLabel("Parking Spot:")); infoPanel.add(lblSpot);
        infoPanel.add(new JLabel("Vehicle Type:")); infoPanel.add(lblType);
        infoPanel.add(new JLabel("Entry Time:")); infoPanel.add(lblEntry);
        infoPanel.add(new JLabel("Exit Time:")); infoPanel.add(lblExit);
        infoPanel.add(new JLabel("Billable hour(s):")); infoPanel.add(lblBillableHours);

        infoPanel.add(new JLabel("Parking Fee:")); infoPanel.add(lblFee);
        infoPanel.add(new JLabel("Fine:")); infoPanel.add(lblFine);
        infoPanel.add(new JLabel("Previous Debt")); infoPanel.add(lblDebt);
        infoPanel.add(new JLabel("Total Paid:")); infoPanel.add(lblTotal);
        infoPanel.add(new JLabel("Balance")); infoPanel.add(lblBalance); // empty row for spacing

        Font font = new Font("Arial", Font.BOLD, 14);
        lblPlate.setForeground(Color.BLUE); lblPlate.setFont(font);
        lblSpot.setForeground(Color.RED);   lblSpot.setFont(font);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        centerPanel.add(infoPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton btnBack = new JButton("Back to Main Menu");
        Config.styleButton(btnBack, Config.COLOR_PRIMARY, Config.BTN_SIZE_MEDIUM);

        btnBack.addActionListener(e -> {
            new EntryExitView().setVisible(true);
            dispose();
        });
        btnPanel.add(btnBack);
        add(btnPanel, BorderLayout.SOUTH);

        if (receiptCombo.getItemCount() > 0) receiptCombo.setSelectedIndex(0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void displayReceiptDetails() {
        Ticket selected = (Ticket) receiptCombo.getSelectedItem();
        if (selected != null) {

            double balance = selected.getTotalPayAmount() - selected.getAmountPaid();
            double billableHours = Math.ceil(selected.getHourParked());
            if (billableHours == 0) billableHours = 1;

            lblPlate.setText(selected.getPlate());
            lblSpot.setText(selected.getSpotID());
            lblType.setText(selected.getVehicleType());
            lblEntry.setText(selected.getEntryTimeStr());
            lblExit.setText(selected.getExitTimeStr());
            lblBillableHours.setText(billableHours + " hour(s)");

            lblFee.setText(String.format("RM %.2f", selected.getParkingFeeAmount()));
            lblFine.setText(String.format("RM %.2f", selected.getFineAmount()));
            lblDebt.setText(String.format("RM %.2f", selected.getPreviousDebt()));
            lblTotal.setText(String.format("RM %.2f", selected.getTotalPayAmount()));

            lblBalance.setText(String.format("RM %.2f", balance));
        }
    }

    private void loadReceipts() {
        TicketController ticketController = TicketController.getInstance();
        List<Ticket> receipts = ticketController.getAllReceipts();
        for (Ticket t : receipts) {
            receiptCombo.addItem(t);
        }
    }
}
