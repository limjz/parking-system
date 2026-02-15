package views;  

import java.awt.*;
import javax.swing.*;
import utils.Config;

public class EntryExitView extends JFrame {


    public EntryExitView() {
        super("Customer Portal");

        setSize(400, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // ---------------- Button ------------------
        JButton EntryButton = new JButton("Entry");
        Config.styleButton(EntryButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);
        gbc.gridy = 1;
        panel.add(EntryButton, gbc);

        JButton reserveButton = new JButton("Reserve");
        Config.styleButton(reserveButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);
        gbc.gridy = 2;
        panel.add(reserveButton, gbc);

        JButton ExitButton = new JButton("Exit");
        Config.styleButton(ExitButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);
        gbc.gridy = 3;
        panel.add(ExitButton, gbc);

        JButton TicketButton = new JButton("View Ticket");
        Config.styleButton(TicketButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);
        // TicketButton.setPreferredSize(btnSize);
        gbc.gridy = 4;
        panel.add(TicketButton, gbc);

        JButton receiptButton = new JButton("View Receipt");
        Config.styleButton(receiptButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);
        gbc.gridy = 5;
        panel.add(receiptButton, gbc);

        JButton adminLoginButton = new JButton("Admin Login"); 
        Config.styleButton(adminLoginButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_STANDARD);
        // adminLoginButton.setPreferredSize(btnSize);
        gbc.gridy = 6;
        panel.add(adminLoginButton, gbc);

        // ------------ Action Listeners ------------------
        EntryButton.addActionListener(e -> {
            EntryPage entryPage = new EntryPage();
            entryPage.setVisible(true);
            this.setVisible(false);
        });        

        reserveButton.addActionListener(e -> {
            new ReservePage().setVisible(true);
            this.setVisible(false);
        });

        ExitButton.addActionListener(e -> {
            new ExitPage().setVisible(true); 
            this.setVisible(false);
        });

        TicketButton.addActionListener(e -> {
            new TicketView().setVisible(true); 
            this.setVisible(false);
        });

        receiptButton.addActionListener(e -> {
            new ReceiptView().setVisible(true);
            this.setVisible(false);
        });

        adminLoginButton.addActionListener(e-> {
            new LoginPage().setVisible(true);
            this.setVisible(false);
        });

        add(panel);

        // pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}