package com.medivault.ui;

import com.medivault.util.LogUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * DashboardUI.java — Refactored to include a fully populated Dashboard Home Screen
 * complete with dynamic summary metrics and a live activity queue table.
 */
public class DashboardUI extends JFrame {

    private final String userRole;
    private JPanel contentCanvas;
    private CardLayout cardLayout;

    // Component fields for the Doctor Schedule tab
    private JTable doctorTable;
    private DefaultTableModel doctorTableModel;

    // Component fields for the Home View Queue Table
    private JTable queueTable;
    private DefaultTableModel queueTableModel;

    // Form inputs for scheduling appointments
    private JComboBox<String> doctorDropdown;
    private JComboBox<String> dayDropdown;
    private JComboBox<String> timeDropdown;

    public DashboardUI(String role) {
        this.userRole = role.toLowerCase().trim();
        buildUI();
    }
    private final com.medivault.dao.PatientDAO patientDAO = new com.medivault.dao.PatientDAO();

    private void buildUI() {
        setTitle("MediVault Workspace — Signed in as: " + userRole.toUpperCase());
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        setLayout(new BorderLayout());

        // ==========================================
        //  1. SIDEBAR PANEL (Left Column - Navigation)
        // ==========================================
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(25, 45, 75));
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));

        JLabel appTitle = new JLabel("MediVault Engine");
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(appTitle);

        JLabel userStatus = new JLabel("Access Level: " + userRole.toUpperCase());
        userStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userStatus.setForeground(new Color(170, 195, 225));
        userStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(userStatus);

        sidebar.add(Box.createRigidArea(new Dimension(0, 35)));
        sidebar.add(new JSeparator());
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton welcomeTabBtn  = createNavButton("🏠 Dashboard Home");
        JButton addPatientBtn  = createNavButton("➕ Add Patient Record");
        JButton viewPatientBtn = createNavButton("📋 View Patient Logs");
        JButton bookAppointBtn = createNavButton("📅 Book Appointment");
        JButton logoutBtn      = createNavButton("🚪 Logout Account");

        sidebar.add(welcomeTabBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        if (userRole.equals("patient")) {
            sidebar.add(bookAppointBtn);
        } else {
            sidebar.add(addPatientBtn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
            sidebar.add(viewPatientBtn);
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);

        // ==========================================
        //  2. CONTENT CANVAS (Right Panel - Multi-View)
        // ==========================================
        cardLayout = new CardLayout();
        contentCanvas = new JPanel(cardLayout);
        contentCanvas.setBackground(Color.WHITE);

        // ------------------------------------------
        //  VIEW A: DASHBOARD HOME SCREEN (POPULATED)
        // ------------------------------------------
        JPanel welcomeView = new JPanel(new BorderLayout(0, 25));
        welcomeView.setBackground(Color.WHITE);
        welcomeView.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        // Home Top Headers Panel
        JPanel homeHeaderPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        homeHeaderPanel.setBackground(Color.WHITE);
        JLabel homeHeading = new JLabel("Welcome back to MediVault");
        homeHeading.setFont(new Font("SansSerif", Font.BOLD, 26));
        homeHeading.setForeground(new Color(40, 40, 40));
        JLabel homeSubheading = new JLabel("Real-time clinical management overview and tracking logs.");
        homeSubheading.setFont(new Font("SansSerif", Font.PLAIN, 13));
        homeSubheading.setForeground(Color.GRAY);
        homeHeaderPanel.add(homeHeading);
        homeHeaderPanel.add(homeSubheading);
        welcomeView.add(homeHeaderPanel, BorderLayout.NORTH);

        // Home Center Body: Split into Metric Cards (Top) and Live Queue (Bottom)
        JPanel homeBodyPanel = new JPanel(new BorderLayout(0, 25));
        homeBodyPanel.setBackground(Color.WHITE);

        // 1. Horizontal Summary KPI Cards Container
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        metricsPanel.setBackground(Color.WHITE);

// 1. Fetch live calculations from your database layers
        int actualPatientCount = patientDAO.getTotalPatientCount();
        int actualDoctorCount  = patientDAO.getActiveDoctorsCount();
        int actualPendingCount = patientDAO.getPendingBookingsCount();

// 2. Dynamically pass those values into your metric cards instead of hardcoded numbers
        metricsPanel.add(createMetricCard("Total Registered Patients", String.valueOf(actualPatientCount), new Color(30, 90, 160)));
        metricsPanel.add(createMetricCard("Active Practitioners On Duty", String.valueOf(actualDoctorCount), new Color(34, 139, 34)));
        metricsPanel.add(createMetricCard("Pending System Bookings", String.valueOf(actualPendingCount), new Color(200, 80, 40)));

        homeBodyPanel.add(metricsPanel, BorderLayout.NORTH);

        // 2. Queue Tracking Sub-panel Container
        JPanel queuePanel = new JPanel(new BorderLayout(0, 10));
        queuePanel.setBackground(Color.WHITE);

        JLabel queueTitle = new JLabel("Upcoming Clinical Check-ins & Appointments Queue");
        queueTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        queueTitle.setForeground(new Color(60, 70, 90));
        queuePanel.add(queueTitle, BorderLayout.NORTH);

        String[] queueColumns = {"Appt ID", "Patient Name", "Assigned Doctor", "Scheduled Slot", "Urgency", "Status"};
        queueTableModel = new DefaultTableModel(queueColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        queueTable = new JTable(queueTableModel);
        queueTable.setRowHeight(32);
        queueTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        queueTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        queueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane queueScrollPane = new JScrollPane(queueTable);
        queuePanel.add(queueScrollPane, BorderLayout.CENTER);

        homeBodyPanel.add(queuePanel, BorderLayout.CENTER);
        welcomeView.add(homeBodyPanel, BorderLayout.CENTER);

        // ------------------------------------------
        //  VIEW B: APPOINTMENT HUB SPLIT PANEL (For Patients)
        // ------------------------------------------
        JPanel appointmentHubView = new JPanel(new GridLayout(1, 2, 30, 0));
        appointmentHubView.setBackground(Color.WHITE);
        appointmentHubView.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Left Booking Form Pane
        JPanel formPane = new JPanel(new GridBagLayout());
        formPane.setBackground(new Color(248, 250, 253));
        formPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 240), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        JLabel formHeader = new JLabel("Schedule Appointment");
        formHeader.setFont(new Font("SansSerif", Font.BOLD, 20));
        formHeader.setForeground(new Color(30, 50, 80));
        formPane.add(formHeader, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 5, 20, 5);
        JLabel formSub = new JLabel("Select your practitioner and preferred timing slot.");
        formSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        formSub.setForeground(Color.GRAY);
        formPane.add(formSub, gbc);

        gbc.gridwidth = 1; gbc.gridy = 2; gbc.insets = new Insets(8, 5, 8, 5);
        formPane.add(new JLabel("Choose Doctor:"), gbc);
        doctorDropdown = new JComboBox<>(new String[]{"Dr. Arsh Sharma", "Dr. Shalini Katoch", "Dr. Rahul Verma", "Dr. Neha Thakur"});
        gbc.gridx = 1; formPane.add(doctorDropdown, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPane.add(new JLabel("Select Day:"), gbc);
        dayDropdown = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});
        gbc.gridx = 1; formPane.add(dayDropdown, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPane.add(new JLabel("Time Window:"), gbc);
        timeDropdown = new JComboBox<>(new String[]{"09:00 AM - 11:00 AM", "11:00 AM - 01:00 PM", "02:00 PM - 04:00 PM", "04:00 PM - 06:00 PM"});
        gbc.gridx = 1; formPane.add(timeDropdown, gbc);

        JButton submitBookingBtn = new JButton("Confirm Booking Request");
        LoginUI.styleButton(submitBookingBtn, new Color(34, 139, 34));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.insets = new Insets(25, 5, 5, 5);
        formPane.add(submitBookingBtn, gbc);

        // Right Live Roster Data View Grid
        JPanel rosterPane = new JPanel(new BorderLayout(0, 10));
        rosterPane.setBackground(Color.WHITE);

        JLabel rosterHeader = new JLabel("Live Availability Reference Panel");
        rosterHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        rosterHeader.setForeground(Color.GRAY);
        rosterPane.add(rosterHeader, BorderLayout.NORTH);

        String[] columns = {"Doctor Name", "Specialization", "Active Days", "Room No"};
        doctorTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        doctorTable = new JTable(doctorTableModel);
        doctorTable.setRowHeight(32);
        doctorTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        rosterPane.add(scrollPane, BorderLayout.CENTER);

        appointmentHubView.add(formPane);
        appointmentHubView.add(rosterPane);

        // Map Views to canvas
        contentCanvas.add(welcomeView, "WelcomeCard");
        contentCanvas.add(appointmentHubView, "AppointmentCard");

        add(sidebar, BorderLayout.WEST);
        add(contentCanvas, BorderLayout.CENTER);

        // Run boot data mapping simulators
        loadDoctorRosterReference();
        loadHomeDashboardQueue();

        // ==========================================
        //  3. ACTION EVENT BINDINGS
        // ==========================================
        welcomeTabBtn.addActionListener(e -> cardLayout.show(contentCanvas, "WelcomeCard"));

        bookAppointBtn.addActionListener(e -> {
            LogUtil.log("Patient navigated to Appointment Booking interface.");
            cardLayout.show(contentCanvas, "AppointmentCard");
        });

        addPatientBtn.addActionListener(e -> new AddPatientUI().setVisible(true));
        viewPatientBtn.addActionListener(e -> new ViewPatientUI(userRole).setVisible(true));

        logoutBtn.addActionListener(e -> {
            new LoginUI().setVisible(true);
            dispose();
        });

        submitBookingBtn.addActionListener(e -> executeAppointmentBooking());
    }

    private void executeAppointmentBooking() {
        String doc = (String) doctorDropdown.getSelectedItem();
        String day = (String) dayDropdown.getSelectedItem();
        String time = (String) timeDropdown.getSelectedItem();

        LogUtil.log("Appointment request submitted: " + doc + " on " + day + " during " + time);

        JOptionPane.showMessageDialog(this,
                "Appointment booked successfully with " + doc + " for " + day + " (" + time + ").\nStatus: PENDING CONFIRMATION",
                "Appointment Scheduled",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Fills the Home Screen Queue Table with mockup check-in data loops
     */
    private void loadHomeDashboardQueue() {
        queueTableModel.setRowCount(0);
        Object[][] mockQueueData = {
                {"APT-9821", "Aman Sharma", "Dr. Rahul Verma", "Today (10:30 AM)", "Medium", "Confirmed"},
                {"APT-9822", "Rishav Rana", "Dr. Arsh Sharma", "Today (11:15 AM)", "Routine", "Checked In"},
                {"APT-9823", "Komal Thakur", "Dr. Shalini Katoch", "Today (12:00 PM)", "High", "In Progress"},
                {"APT-9824", "Vikram Singh", "Dr. Neha Thakur", "Tomorrow (03:00 PM)", "Routine", "Pending"}
        };
        for (Object[] row : mockQueueData) {
            queueTableModel.addRow(row);
        }
    }

    private void loadDoctorRosterReference() {
        doctorTableModel.setRowCount(0);
        Object[][] rosterData = {
                {"Dr. Arsh Sharma", "Cardiology", "Mon, Wed, Fri", "Cabin 102"},
                {"Dr. Shalini Katoch", "Pediatrics", "Tue, Thu, Sat", "Cabin 105"},
                {"Dr. Rahul Verma", "General Medicine", "Mon to Sat", "OPD Block A"},
                {"Dr. Neha Thakur", "Neurology", "Wed, Fri", "Cabin 201"}
        };
        for (Object[] row : rosterData) {
            doctorTableModel.addRow(row);
        }
    }

    /**
     * UI Component Factory: Generates clean, stylized KPI indicator summary cards
     */
    private JPanel createMetricCard(String title, String value, Color colorLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(new Color(250, 251, 254));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 245), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLbl.setForeground(Color.GRAY);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("SansSerif", Font.BOLD, 32));
        valLbl.setForeground(colorLabel);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLbl, BorderLayout.SOUTH);
        return card;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(35, 60, 95));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMargin(new Insets(0, 15, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(45, 80, 125)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(35, 60, 95)); }
        });
        return btn;
    }
}