package views;  

import java.awt.*;
import javax.swing.*;
import models.VehicleType;
import utils.Config;

public class EntryPage extends JFrame {

    public EntryPage() {
        super("Entry Terminal");
        setSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT/2);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Plate
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("License Plate:"), gbc);
        JTextField txtLicense = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0; add(txtLicense, gbc);

        // Row 1: Vehicle Type
        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Vehicle Type:"), gbc);
        // use the enum from VehicleType to load the drop down
        JComboBox<VehicleType> vehicleTypeCombo = new JComboBox<>(VehicleType.values());
        gbc.gridx = 1; gbc.gridy = 1; add(vehicleTypeCombo, gbc);     

        // Row 3: Handicapped Card Holder
        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Handicapped Card Holder"), gbc);
        JCheckBox chkHasCard = new JCheckBox("Yes");
        chkHasCard.setEnabled(false); // Disabled by default
        gbc.gridx = 1; gbc.gridy = 2; add(chkHasCard, gbc);

        // --------- Jcombo Action listener ---------
        vehicleTypeCombo.addActionListener(e -> {
            VehicleType selected = (VehicleType) vehicleTypeCombo.getSelectedItem();
            
            if (selected == VehicleType.HANDICAPPED) {
                chkHasCard.setEnabled(true); //only handicapped car can have card

            } else {
                chkHasCard.setSelected(false); // reset
                chkHasCard.setEnabled(false); // disable if not handicapped
            }
        });

        // ------------ Buttons ------------
        JPanel btnPanel = new JPanel();
        JButton backButton = new JButton("Back");
        JButton nextButton = new JButton("Next");

        // button style 
        Config.styleButton(backButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_SMALL);
        Config.styleButton(nextButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_SMALL);

        btnPanel.add(backButton);
        btnPanel.add(nextButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(btnPanel, gbc);
        
        // --------- Button Action Listener ----------
        backButton.addActionListener(e -> {
            new EntryExitView().setVisible(true);
            dispose();
        });

        nextButton.addActionListener(e -> {
            String plate = txtLicense.getText();

            // convert enum to string for filehandler 
            VehicleType selectedType = (VehicleType) vehicleTypeCombo.getSelectedItem(); 
            
            boolean isHandicappedVehicle = (selectedType == VehicleType.HANDICAPPED); // only if the vehicle type is handicapped, then it can be a handicapped vehicle
            boolean hasCard = chkHasCard.isSelected() && chkHasCard.isEnabled(); // only consider if the checkbox is enabled (which means they are handicapped)

            if(plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a license plate.");
                return;
            }

            // pass enum straight to parkingPage // selectedType
            ParkingPage parkingPage = new ParkingPage(plate, selectedType, isHandicappedVehicle, hasCard);
            parkingPage.setVisible(true);
            this.setVisible(false);
        });
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}