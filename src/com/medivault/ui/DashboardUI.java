package com.medivault.ui;

import com.medivault.util.LogUtil;

import javax.swing.*;
import java.awt.*;

/**
 * DashboardUI.java — Updated for Role-Based Access Control
 */
public class DashboardUI extends JFrame {

    // Field to store the role passed from LoginUI
    private final String userRole;

    // Updated constructor to accept the role
    public DashboardUI(String role) {
        this.userRole = role;
        buildUI();
    }

    private void buildUI() {
        // Show the role in the title bar for clarity
        setTitle("MediVault — Dashboard (" + userRole + ")");
        setSize(320, 290);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.fill  = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 0, 8, 0);

        // Dynamic Title based on role
        JLabel title = new JLabel("Logged in as: " + userRole, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(new Color(30, 90, 160));
        g.gridx = 0; g.gridy = 0;
        panel.add(title, g);

        g.gridy = 1;
        panel.add(new JSeparator(), g);

        JButton addBtn  = makeBtn("Add Patient",   new Color(34, 130, 34));
        JButton viewBtn = makeBtn("View Patients", new Color(30, 90, 160));
        JButton exitBtn = makeBtn("Exit",           new Color(170, 50, 50));

        g.gridy = 2; panel.add(addBtn,  g);
        g.gridy = 3; panel.add(viewBtn, g);
        g.gridy = 4; panel.add(exitBtn, g);

        add(panel);

        addBtn.addActionListener(e -> {
            LogUtil.log("Opened: Add Patient screen.");
            new AddPatientUI().setVisible(true);
        });

        viewBtn.addActionListener(e -> {
            LogUtil.log("Opened: View Patients screen.");
            // CRITICAL: Pass the userRole to ViewPatientUI so it can enforce RBAC
            new ViewPatientUI(userRole).setVisible(true);
        });

        exitBtn.addActionListener(e -> {
            LogUtil.log("Application exited by user.");
            System.exit(0);
        });
    }

    private JButton makeBtn(String text, Color color) {
        JButton btn = new JButton(text);
        LoginUI.styleButton(btn, color);
        btn.setPreferredSize(new Dimension(200, 36));
        return btn;
    }
}