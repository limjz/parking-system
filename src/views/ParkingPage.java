package views;  

import java.awt.*;
import javax.swing.*;
import utils.Config;

public class ParkingPage extends JFrame {

    private JFrame previousScreen;
    private GridBagConstraints gbc;
    

    public ParkingPage() {

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        setSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Floor 1", createFloorPanel("F1"));
        tabbedPane.addTab("Floor 2", createFloorPanel("F2"));
        tabbedPane.addTab("Floor 3", createFloorPanel("F3"));
        tabbedPane.addTab("Floor 4", createFloorPanel("F4"));
        tabbedPane.addTab("Floor 5", createFloorPanel("F5"));

        add(tabbedPane, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JPanel createFloorPanel(String floorLabel) {

        JPanel floorPanel = new JPanel(new GridBagLayout());


        gbc.gridx = 0; gbc.gridy = 0;
        floorPanel.add(new JLabel("Placeholder") , gbc);
        return floorPanel;
    }

}
