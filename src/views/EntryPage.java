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

        // Row 2: Is Handicapped?
        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Is Handicapped?"), gbc);
        JCheckBox chkIsHandicapped = new JCheckBox("Yes");
        gbc.gridx = 1; gbc.gridy = 2; add(chkIsHandicapped, gbc);

        // Row 3: Handicapped Card Holder
        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Handicapped Card Holder"), gbc);
        JCheckBox chkHasCard = new JCheckBox("Yes");
        chkHasCard.setEnabled(false); // Disabled by default
        gbc.gridx = 1; gbc.gridy = 3; add(chkHasCard, gbc);

        //Motorcycle cannot park at handicapped spot 
        // --------- Jcombo Action listener ---------
        vehicleTypeCombo.addActionListener(e -> {
            VehicleType selected = (VehicleType) vehicleTypeCombo.getSelectedItem();
            
            if (selected == VehicleType.MOTORCYCLE) {
                // Disable and Reset Handicapped options
                chkIsHandicapped.setSelected(false);
                chkIsHandicapped.setEnabled(false);
                
                chkHasCard.setSelected(false);
                chkHasCard.setEnabled(false);
            } else {
                // Re-enable the main checkbox for Cars/SUVs
                chkIsHandicapped.setEnabled(true);
            }
        });
        
        // --- Enable Card Checkbox only if Handicapped is selected ---
        chkIsHandicapped.addActionListener(e -> {
            if (chkIsHandicapped.isSelected()) {
                chkHasCard.setEnabled(true);
            } else {
                chkHasCard.setEnabled(false);
                chkHasCard.setSelected(false); 
            }
        });

        // Trigger logic immediately (incase default is Motorcycle)
        if (vehicleTypeCombo.getSelectedItem() == VehicleType.MOTORCYCLE) {
             chkIsHandicapped.setEnabled(false);
        }

        // --------- Buttons ---------
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
            
            boolean isHandicappedPerson = chkIsHandicapped.isSelected();
            boolean hasCard = chkHasCard.isSelected();

            if(plate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a license plate.");
                return;
            }

            // pass enum straight to parkingPage // selectedType
            ParkingPage parkingPage = new ParkingPage(plate, selectedType, isHandicappedPerson, hasCard);
            parkingPage.setVisible(true);
            this.setVisible(false);
        });
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}