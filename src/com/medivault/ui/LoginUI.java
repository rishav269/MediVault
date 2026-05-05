package com.medivault.ui;


import com.medivault.dao.userDAO;
import com.medivault.util.LogUtil;

import javax.swing.*;
import java.awt.*;

/**
 * LoginUI.java — Updated for Database-Backed Login and RBAC
 */
public class LoginUI extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    // Call the new UserDAO for database checks
    private final userDAO userDAO = new userDAO();

    public LoginUI() {
        buildUI();
    }

    private void buildUI() {
        setTitle("MediVault — Login");
        setSize(380, 240);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 30, 18, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 7, 7, 7);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("MediVault", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(30, 90, 160));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        panel.add(title, g);

        g.gridwidth = 1; g.gridy = 1; g.gridx = 0;
        panel.add(new JLabel("Username:"), g);
        usernameField = new JTextField(14);
        g.gridx = 1;
        panel.add(usernameField, g);

        g.gridy = 2; g.gridx = 0;
        panel.add(new JLabel("Password:"), g);
        passwordField = new JPasswordField(14);
        g.gridx = 1;
        panel.add(passwordField, g);

        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn, new Color(30, 90, 160));
        g.gridy = 3; g.gridx = 0; g.gridwidth = 2;
        panel.add(loginBtn, g);

        add(panel);

        loginBtn.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    /**
     * Reads database via UserDAO to validate user and determine role.
     */
    private void handleLogin() {
        try {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            // [DATABASE LOGIN] Validate against the MySQL 'users' table
            String role = userDAO.validateLogin(user, pass);

            if (role != null) {
                LogUtil.log("Login successful — user: " + user + " | role: " + role);

                // [RBAC] Pass the role to the Dashboard to manage permissions
                new DashboardUI(role).setVisible(true);
                dispose();

            } else {
                LogUtil.log("Login failed — invalid credentials for user: " + user);
                JOptionPane.showMessageDialog(this,
                        "Incorrect username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                passwordField.requestFocus();
            }

        } catch (Exception ex) {
            LogUtil.log("ERROR: unexpected error in LoginUI — " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            LogUtil.log("MediVault application started.");
            new LoginUI().setVisible(true);
        });
    }
}