package views;  

import java.awt.*;
import javax.swing.*;
import utils.Config;

public class EntryExitView extends JFrame {

    //private EntryExitController controller;

    public EntryExitView() {
        super("Customer Portal");
        //this.controller = new EntryExitController();

        setSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        Dimension btnSize = new Dimension(175, 25);

        // Buttons
        JButton EntryButton = new JButton("Entry");
        EntryButton.setPreferredSize(btnSize);
        gbc.gridy = 1;
        panel.add(EntryButton, gbc);

        JButton ExitButton = new JButton("Exit");
        ExitButton.setPreferredSize(btnSize);
        gbc.gridy = 2;
        panel.add(ExitButton, gbc);

        JButton TicketButton = new JButton("View Ticekt");
        TicketButton.setPreferredSize(btnSize);
        gbc.gridy = 3;
        panel.add(TicketButton, gbc);

        // Action Listeners
        EntryButton.addActionListener(e -> {
            EntryPage entryPage = new EntryPage();
            entryPage.setVisible(true);
            this.setVisible(false);
        });        

        ExitButton.addActionListener(e -> {

        });

        TicketButton.addActionListener(e -> {

        });

        add(panel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}