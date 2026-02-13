package views;

import controllers.AdminController;
import java.awt.*;
import javax.swing.*;
import utils.Config;

public class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton resetButton;
    private final JButton backButton;

    private int attemptsLeft = 3;

    public LoginPage() {
        setTitle("Login - Parking System");
        setSize(420, 260);

        setLayout(new BorderLayout(10, 10));

        //--------------- Title ---------------
        JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        // --------------- Form ---------------
        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        form.add(new JLabel("Username:"));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        add(form, BorderLayout.CENTER);

        // --------------- Buttons --------------- 
        loginButton = new JButton("Login");
        resetButton = new JButton("Reset");
        backButton = new JButton("Back"); 

        // button style
        Config.styleButton(loginButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_MINI);
        Config.styleButton(resetButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_MINI);
        Config.styleButton(backButton, Config.COLOR_PRIMARY, Config.BTN_SIZE_MINI);

        resetButton.setEnabled(false); // disabled by default

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(backButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);

        add(buttonPanel, BorderLayout.SOUTH);

        //--------------- Actions Listeners ---------------
        backButton.addActionListener(e -> backToUserPage());
        loginButton.addActionListener(e -> handleLogin());
        resetButton.addActionListener(e -> resetLogin());

        // Press Enter to login
        getRootPane().setDefaultButton(loginButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void backToUserPage (){ 

        dispose();
        EntryExitView eev = new EntryExitView(); 
        eev.setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        AdminController adminController = new AdminController();

        if (adminController.isLogin(username, password)) {
            // Successful login
            new AdminPage();
            dispose();
            return;
        }

        // Failed login 
        attemptsLeft--;
        passwordField.setText(""); // clear password for security

        if (attemptsLeft <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Too many failed attempts.\nLogin has been disabled.",
                    "Login Locked",
                    JOptionPane.ERROR_MESSAGE
            );
            lockLogin();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password.\nAttempts left: " + attemptsLeft,
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void lockLogin() {
        loginButton.setEnabled(false);
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);
        resetButton.setEnabled(true);
    }

    private void resetLogin() {
        attemptsLeft = 3;

        usernameField.setText("");
        passwordField.setText("");

        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
        loginButton.setEnabled(true);
        resetButton.setEnabled(false);

        JOptionPane.showMessageDialog(
                this,
                "Login has been reset.\nYou may try again.",
                "Login Reset",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


}
