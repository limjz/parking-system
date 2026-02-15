package views;

import java.awt.*;
import javax.swing.*;
import utils.Config;
import utils.FileHandler;

public class ReservePage extends JFrame {

    public ReservePage() {
        super("Reserve VIP");
        setSize(Config.WINDOW_WIDTH / 2, Config.WINDOW_HEIGHT / 2);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Car Plate
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Car Plate:"), gbc);
        JTextField txtPlate = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        add(txtPlate, gbc);

        // Buttons
        JPanel btnPanel = new JPanel();
        JButton backButton = new JButton("Back");
        JButton saveButton = new JButton("Save");

        Config.styleButton(backButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_SMALL);
        Config.styleButton(saveButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_SMALL);

        btnPanel.add(backButton);
        btnPanel.add(saveButton);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(btnPanel, gbc);

        backButton.addActionListener(e -> {
            new EntryExitView().setVisible(true);
            dispose();
        });

        saveButton.addActionListener(e -> {
            String plate = txtPlate.getText().trim();
            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a car plate.");
                return;
            }
            FileHandler.appendData(Config.VIP_FILE, plate);
            JOptionPane.showMessageDialog(this, "VIP plate saved.");
            new EntryExitView().setVisible(true);
            dispose();
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
