package views;  

import java.awt.*;
import javax.swing.*;
import utils.Config;

public class EntryPage extends JFrame {

    private JFrame previousScreen;

    public EntryPage() {

        setSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("License Plate:"), gbc);
        JTextField txtLicense = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0; add(txtLicense, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Vehicle Type:"), gbc);
        String[] types = {"Motorcycle", "Car", "SUV/Truck", "Handicapped Vehicle"};
        JComboBox<String> vehicleTypeCombo = new JComboBox<>(types);
        gbc.gridx = 1; gbc.gridy = 1; add(vehicleTypeCombo, gbc);     

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Handicapped Card Holder"), gbc);
        JCheckBox handicappedCheckBox = new JCheckBox("Yes");
        gbc.gridx = 1; gbc.gridy = 2; add(handicappedCheckBox, gbc);

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton btnPrevious = new JButton("Previous");
        JButton btnNext = new JButton("Next");
        btnPanel.add(btnPrevious);
        btnPanel.add(btnNext);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(btnPanel, gbc);
        
        // Button Logic
        btnPrevious.addActionListener(e -> {
        dispose();
        if (previousScreen != null) previousScreen.setVisible(true);
        });

        btnNext.addActionListener(e -> {
            ParkingPage parkingPage = new ParkingPage();
            parkingPage.setVisible(true);
            this.setVisible(false);
        });

    }
}