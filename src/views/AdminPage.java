package views;

import controllers.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminPage extends JFrame {

    private final AdminController adminController = new AdminController();

    private final JLabel schemeLabel = new JLabel();
    private final JComboBox<FineSchemeType> schemeBox =
            new JComboBox<>(FineSchemeType.values());
    private final JButton applyBtn = new JButton("Apply");

    private final JTextArea output = new JTextArea();
    private final JLabel statusBar = new JLabel("Ready.");

    public AdminPage() {
        setTitle("University Parking Lot Management System — Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildLeftMenu(), BorderLayout.WEST);
        add(buildOutputPanel(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        loadCurrentSchemeToUI();

        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // HEADER 
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel("University Parking Lot Management System — Admin");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JPanel schemePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        schemePanel.add(new JLabel("Fine Scheme: "));
        schemePanel.add(schemeLabel);
        schemePanel.add(schemeBox);
        schemePanel.add(applyBtn);

        applyBtn.addActionListener(e -> {
            FineSchemeType selected = (FineSchemeType) schemeBox.getSelectedItem();
            if (selected == null) selected = FineSchemeType.FIXED;

            adminController.updateFineScheme(selected);
            schemeLabel.setText(selected.name());
            setStatus("Fine scheme updated to " + selected.name() + " (future entries only).");
            output.append("\n[INFO] Fine scheme updated to " + selected.name() + " (future entries only)\n");
        });

        header.add(title, BorderLayout.WEST);
        header.add(schemePanel, BorderLayout.EAST);
        return header;
    }

    // LEFT MENU 
    private JPanel buildLeftMenu() {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createTitledBorder("Actions"));

        // Make the left side wider so everything fits nicely
        menu.setPreferredSize(new Dimension(320, 0));

        // Parking Structure 
        JLabel psLabel = new JLabel("Parking Structure");
        psLabel.setFont(psLabel.getFont().deriveFont(Font.BOLD));
        psLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menu.add(psLabel);
        menu.add(Box.createVerticalStrut(8));

        JButton overviewBtn = new JButton("Overview (All Floors)");
        overviewBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        overviewBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        overviewBtn.addActionListener(e -> {
            output.setText(adminController.generateAllFloorsOverview());
            setStatus("Showing: Parking Structure Overview");
        });
        menu.add(overviewBtn);
        menu.add(Box.createVerticalStrut(8));

        // Floor buttons F1–F5 (fixed one-row, no wrap) 
        JPanel floorsPanel = new JPanel(new GridLayout(1, 5, 8, 8));
        floorsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fixed width so GridLayout doesn't squeeze text into "..."
        floorsPanel.setPreferredSize(new Dimension(300, 42));
        floorsPanel.setMaximumSize(new Dimension(300, 42));

        JButton f1 = new JButton("F1");
        JButton f2 = new JButton("F2");
        JButton f3 = new JButton("F3");
        JButton f4 = new JButton("F4");
        JButton f5 = new JButton("F5");

        styleFloorButton(f1);
        styleFloorButton(f2);
        styleFloorButton(f3);
        styleFloorButton(f4);
        styleFloorButton(f5);

        f1.addActionListener(e -> showFloor(1));
        f2.addActionListener(e -> showFloor(2));
        f3.addActionListener(e -> showFloor(3));
        f4.addActionListener(e -> showFloor(4));
        f5.addActionListener(e -> showFloor(5));

        floorsPanel.add(f1);
        floorsPanel.add(f2);
        floorsPanel.add(f3);
        floorsPanel.add(f4);
        floorsPanel.add(f5);

        menu.add(floorsPanel);

        menu.add(Box.createVerticalStrut(14));
        JSeparator sep1 = new JSeparator();
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        menu.add(sep1);
        menu.add(Box.createVerticalStrut(10));

        // Other Reports 
        menu.add(makeMenuButton("Occupancy Rate",
                () -> adminController.generateReport(ReportType.OCCUPANCY_RATE)));
        menu.add(Box.createVerticalStrut(8));

        menu.add(makeMenuButton("Revenue",
                () -> adminController.generateReport(ReportType.REVENUE)));
        menu.add(Box.createVerticalStrut(8));

        menu.add(makeMenuButton("Current Vehicles",
                () -> adminController.generateReport(ReportType.CURRENT_VEHICLES)));
        menu.add(Box.createVerticalStrut(8));

        menu.add(makeMenuButton("Unpaid Fines",
                () -> adminController.generateReport(ReportType.UNPAID_FINES)));

        menu.add(Box.createVerticalStrut(14));
        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        menu.add(sep2);
        menu.add(Box.createVerticalStrut(10));

        JButton refreshBtn = new JButton("Refresh Current Scheme");
        refreshBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        refreshBtn.addActionListener(e -> {
            loadCurrentSchemeToUI();
            setStatus("Scheme refreshed.");
        });

        JButton clearBtn = new JButton("Clear Output");
        clearBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        clearBtn.addActionListener(e -> {
            output.setText("");
            setStatus("Output cleared.");
        });

        menu.add(refreshBtn);
        menu.add(Box.createVerticalStrut(8));
        menu.add(clearBtn);

        return menu;
    }

    private void styleFloorButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 12f));
    }

    // OUTPUT PANEL 
    private JPanel buildOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Report Output"));

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

    // STATUS BAR 
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(6, 8, 6, 8));
        bar.add(statusBar, BorderLayout.CENTER);
        return bar;
    }

    // ACTIONS 
    private void showFloor(int floor) {
        output.setText(adminController.generateFloorView(floor));
        setStatus("Showing: Floor " + floor + " spot status");
    }

    private JButton makeMenuButton(String label, Supplier<String> action) {
        JButton b = new JButton(label);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        b.addActionListener(e -> {
            output.setText(action.get());
            setStatus("Showing: " + label);
        });
        return b;
    }

    private void loadCurrentSchemeToUI() {
        FineSchemeType current = adminController.getFineSchemeFromFile();
        schemeLabel.setText(current.name());
        schemeBox.setSelectedItem(current);

        if (output.getText().trim().isEmpty()) {
            output.setText(
                    "Welcome, Admin.\n\n" +
                    "Parking Structure supports Floor-by-Floor view (F1–F5).\n" +
                    "Click 'Overview' or a floor button to view spot availability.\n\n" +
                    "Fine scheme changes apply to future entries only.\n"
            );
        }
    }

    private void setStatus(String msg) {
        statusBar.setText(msg);
    }

    // Small functional interface (so we don’t need extra imports)
    private interface Supplier<T> { T get(); }
}
