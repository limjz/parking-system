package views;

import java.awt.*;
import javax.swing.*;
import utils.Config;

public class EntryExitView extends JFrame {

  public EntryExitView() { 
    super("Entry/Exit View");
    setSize (Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
    
    JPanel buttonPanel = new JPanel(new GridBagLayout()); 
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    
    // --------------- Buttons -----------------
    gbc.gridx = 0; 
    gbc.gridy = 0;
    JButton entryButton = new JButton("Entry");
    buttonPanel.add (entryButton, gbc);


    gbc.gridx = 1;
    JButton exitButton = new JButton("Exit");
    buttonPanel.add (exitButton, gbc);

    gbc.gridy = 1;
    JButton ticketButton = new JButton("Show Ticker");
    buttonPanel.add (ticketButton, gbc); 


    JLabel titleLabel = new JLabel ("Welcom to the Car Park System", SwingConstants.CENTER);



    add (titleLabel, gbc);
    add (buttonPanel);


    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

}
