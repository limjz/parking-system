package views;

import controllers.AdminController;
import controllers.FineSchemeType; // Ensure this matches your package
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.AdminReport;
import utils.Config;

public class AdminPage extends JFrame {

    private final AdminController adminController = new AdminController();

    private final JTextArea output = new JTextArea();
    private final JLabel statusBar = new JLabel("Ready.");

    // Fine scheme UI
    private final JLabel schemeLabel = new JLabel();
    private final JComboBox<FineSchemeType> schemeBox = new JComboBox<>(FineSchemeType.values());
    private final JButton applyBtn = new JButton("Apply");

    public AdminPage() {
        setTitle("University Parking Lot Management System — " + Config.adminUser);
   

        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildLeftMenu(), BorderLayout.WEST);
        add(buildOutputPanel(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        // wire apply action once
        applyBtn.addActionListener(e -> {
            FineSchemeType selected = (FineSchemeType) schemeBox.getSelectedItem();
            if (selected == null) selected = FineSchemeType.FIXED;

            adminController.updateFineScheme(selected);
            schemeLabel.setText(selected.name());
            setStatus("fine scheme updated: " + selected.name());

            // show dummy preview
            output.append("\n[preview] scheme changed to " + selected.name() + "\n");
            output.append(adminController.getFineSchemePreview());
            output.append("\n");
        });

        loadCurrentSchemeToUI();

        setMinimumSize(new Dimension(1050, 650));
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // ===== HEADER =====
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 10));
        JLabel title = new JLabel("University Parking Lot Management System — Admin");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        header.add(title, BorderLayout.WEST);
        return header;
    }

    // ===== LEFT MENU =====
    private JPanel buildLeftMenu() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createTitledBorder("Actions"));
        menu.setPreferredSize(new Dimension(340, 0));

        // ---- Fine Scheme ----
        JLabel fsLabel = new JLabel("Fine Scheme");
        fsLabel.setFont(fsLabel.getFont().deriveFont(Font.BOLD));
        fsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu.add(fsLabel);
        menu.add(Box.createVerticalStrut(8));

        JPanel finePanel = new JPanel(new GridLayout(3, 1, 6, 6));
        finePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row1.add(new JLabel("Current scheme: ")); 
        row1.add(schemeLabel);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row2.add(new JLabel("Select scheme: ")); 
        row2.add(schemeBox);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        applyBtn.setPreferredSize(new Dimension(90, 28));
        row3.add(applyBtn);

        finePanel.add(row1); 
        finePanel.add(row2); 
        finePanel.add(row3);

        menu.add(finePanel);

        menu.add(Box.createVerticalStrut(15));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(10));



        // ---- Parking Structure ----
        JLabel psLabel = new JLabel("Parking Structure");
        psLabel.setFont(psLabel.getFont().deriveFont(Font.BOLD));
        psLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu.add(psLabel);
        menu.add(Box.createVerticalStrut(8));

        JButton overviewBtn = new JButton("Overview (All floors)");
        overviewBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        overviewBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        
        overviewBtn.addActionListener(e -> {
            output.setText(adminController.generateAllFloorsOverview());
            setStatus("Showing parking structure overview");
        });

        menu.add(overviewBtn);
        menu.add(Box.createVerticalStrut(8));

        // Floor Buttons
        JPanel floorsPanel = new JPanel(new GridLayout(1, 5, 8, 8));
        floorsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        floorsPanel.setPreferredSize(new Dimension(300, 42));
        floorsPanel.setMaximumSize(new Dimension(300, 42));

        for (int i = 1; i <= 5; i++) {
            JButton fBtn = new JButton("F" + i);
            styleFloorButton(fBtn);
            int floorNum = i;
            fBtn.addActionListener(e -> showFloor(floorNum));
            floorsPanel.add(fBtn);
        }
        menu.add(floorsPanel);

        menu.add(Box.createVerticalStrut(15));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(10));



        // ---- Reports ----
        JLabel rptLabel = new JLabel("Reports");
        rptLabel.setFont(rptLabel.getFont().deriveFont(Font.BOLD));
        rptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu.add(rptLabel);
        menu.add(Box.createVerticalStrut(8));

        menu.add(makeMenuButton("Occupancy rate", () -> adminController.generateReport(AdminReport.Type.OCCUPANCY_RATE)));
        menu.add(Box.createVerticalStrut(6));
        menu.add(makeMenuButton("Revenue", () -> adminController.generateReport(AdminReport.Type.REVENUE)));
        menu.add(Box.createVerticalStrut(6));
        menu.add(makeMenuButton("Current vehicles", () -> adminController.generateReport(AdminReport.Type.CURRENT_VEHICLES)));
        menu.add(Box.createVerticalStrut(6));
        menu.add(makeMenuButton("Unpaid fines", () -> adminController.generateReport(AdminReport.Type.UNPAID_FINES)));

        menu.add(Box.createVerticalStrut(12));

        // ---- Refresh/Clear ----
        JPanel actionRow = new JPanel(new GridLayout(1, 2, 8, 0));
        actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            loadCurrentSchemeToUI();
            setStatus("Scheme refreshed");
        });

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            output.setText("");
            setStatus("Output cleared!");
        });

        actionRow.add(refreshBtn);
        actionRow.add(clearBtn);
        menu.add(actionRow);

        return menu;
    }

    private void styleFloorButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 12f));
    }

    // ===== OUTPUT PANEL =====
    private JPanel buildOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Output"));

        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        output.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(output);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ===== BOTTOM BAR =====
    private JPanel buildBottomBar() {
        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setBorder(new EmptyBorder(6, 0, 0, 0));
        statusBar.setBorder(new EmptyBorder(6, 8, 6, 8));
        bottom.add(statusBar, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(100, 30));
        logoutBtn.addActionListener(e -> {
            new LoginPage();
            dispose();
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.add(logoutBtn);
        bottom.add(right, BorderLayout.EAST);
        return bottom;
    }

    // ===== ACTIONS =====
    private void showFloor(int floor) {
        output.setText(adminController.generateFloorView(floor));
        setStatus("showing floor " + floor + " spot status");
    }

    private JButton makeMenuButton(String label, Supplier<String> action) {
        JButton b = new JButton(label);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        b.addActionListener(e -> {
            output.setText(action.get());
            setStatus("showing " + label);
        });
        return b;
    }

    private void loadCurrentSchemeToUI() {
        FineSchemeType current = adminController.getFineSchemeFromFile();
        schemeLabel.setText(current.name());
        schemeBox.setSelectedItem(current);

        if (output.getText().trim().isEmpty()) {
            output.setText("Welcome, Admin.\n\nFine scheme changes apply to future calculations.");
        }
    }

    private void setStatus(String msg) {
        statusBar.setText(msg);
    }

    private interface Supplier<T> { T get(); }
}