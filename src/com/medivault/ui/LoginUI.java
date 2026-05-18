package com.medivault.ui;

import com.medivault.dao.userDAO;
import com.medivault.util.LogUtil;

import javax.swing.*;
import java.awt.*;

/**
 * LoginUI.java — Refactored for an expanded, modern split-screen layout.
 * Integrates database-backed login, RBAC, and modern FlatLaf UI readiness.
 */
public class LoginUI extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;

    // Call the userDAO for database checks
    private final userDAO userDAO = new userDAO();

    public LoginUI() {
        buildUI();
    }

    private void buildUI() {
        // Step 1: Initialize main frame properties with an expanded canvas size
        setTitle("MediVault — Secure Medical Records System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Use a 1x2 Grid to cleanly split the interface down the center
        setLayout(new GridLayout(1, 2));

        // ==========================================
        //  LEFT PANEL: Professional Brand Showcase
        // ==========================================
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(new Color(30, 90, 160)); // Signature MediVault Dark Blue

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.insets = new Insets(10, 10, 5, 10);

        JLabel brandTitle = new JLabel("MediVault");
        brandTitle.setFont(new Font("SansSerif", Font.BOLD, 38));
        brandTitle.setForeground(Color.WHITE);
        leftPanel.add(brandTitle, leftGbc);

        leftGbc.gridy = 1;
        leftGbc.insets = new Insets(5, 10, 10, 10);
        JLabel brandSubtitle = new JLabel("Secure Record Management System");
        brandSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        brandSubtitle.setForeground(new Color(200, 220, 245));
        leftPanel.add(brandSubtitle, leftGbc);

        // ==========================================
        //  RIGHT PANEL: Clean Minimalistic Input Form
        // ==========================================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form Welcome Message
        JLabel formHeader = new JLabel("Welcome Back");
        formHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        formHeader.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        rightPanel.add(formHeader, gbc);

        JLabel formSubHeader = new JLabel("Please enter your account details below.");
        formSubHeader.setFont(new Font("SansSerif", Font.PLAIN, 12));
        formSubHeader.setForeground(Color.GRAY);
        gbc.gridy = 1;
        rightPanel.add(formSubHeader, gbc);

        // Username Elements
        gbc.gridwidth = 1;
        gbc.gridy = 2; gbc.gridx = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        rightPanel.add(userLabel, gbc);

        usernameField = new JTextField(15);
        usernameField.setPreferredSize(new Dimension(0, 30)); // Added field height padding
        gbc.gridx = 1;
        rightPanel.add(usernameField, gbc);

        // Password Elements
        gbc.gridy = 3; gbc.gridx = 0;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        rightPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setPreferredSize(new Dimension(0, 30));
        gbc.gridx = 1;
        rightPanel.add(passwordField, gbc);

        // Login Button
        JButton loginBtn = new JButton("Login Securely");
        styleButton(loginBtn, new Color(30, 90, 160));
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10); // Push button down slightly
        rightPanel.add(loginBtn, gbc);

        // Assembly of both halves into primary window Frame
        add(leftPanel);
        add(rightPanel);

        // Component Action Binding Execution
        loginBtn.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    /**
     * Reads database via UserDAO to validate user credentials and determine system roles.
     */
    private void handleLogin() {
        try {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // [DATABASE LOGIN] Validate credentials against the MySQL database table
            String role = userDAO.validateLogin(user, pass);

            if (role != null) {
                LogUtil.log("Login successful — user: " + user + " | role: " + role);

                // [RBAC] Pass authenticated role layer straight into Dashboard UI core
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

    /**
     * Styles any button with a modern look, handling custom background colors
     * and fixing the Windows Look-and-Feel wash-out bug.
     */
    public static void styleButton(JButton btn, Color primaryColor) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.WHITE); // Crisp white text
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand pointer icon on hover
        btn.setFocusPainted(false);

        // CRITICAL FIXES FOR WINDOWS LOOK-AND-FEEL:
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder());

        // Calculate dynamic hover and click shades based on the color passed in
        Color hoverColor = primaryColor.brighter();
        Color clickedColor = primaryColor.darker();

        // Custom background rendering engine to force our specific colors
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dynamically select shade depending on user interaction state
                if (btn.getModel().isArmed()) {
                    g2.setColor(clickedColor);
                } else if (btn.getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(primaryColor); // Uses the exact color you passed!
                }

                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 6, 6); // Beautiful modern edges
                g2.dispose();
                super.paint(g, c);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // If you add the FlatLaf jar dependency to your project libraries path,
                // you can uncomment the line below to get beautiful flat modern window rendering:
                // com.formdev.flatlaf.FlatLightLaf.setup();

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            LogUtil.log("MediVault application started.");
            new LoginUI().setVisible(true);
        });
    }
}