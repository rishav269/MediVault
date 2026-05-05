package com.medivault.ui;

import com.medivault.model.Patient;
import com.medivault.service.PatientService;
import com.medivault.util.LogUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ViewPatientUI.java — Updated with CRUD and RBAC support
 */
public class ViewPatientUI extends JFrame {

    private final PatientService service = new PatientService();
    private DefaultTableModel tableModel;
    private JTable table;
    private final String userRole; // Store the role for RBAC checks

    // Constructor now accepts the role from DashboardUI
    public ViewPatientUI(String role) {
        this.userRole = role;
        buildUI();
        loadData();
    }

    private void buildUI() {
        setTitle("MediVault — View Patients (" + userRole + ")");
        setSize(650, 450); // Increased size for new buttons
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(new Color(240, 245, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // ── Top Panel: Search ─────────────────────────────────────
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(240, 245, 255));
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        topPanel.add(new JLabel("Search Name/ID:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────
        String[] cols = {"ID", "Name", "Age", "Phone"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionBackground(new Color(184, 207, 229));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Bottom Panel: Actions ─────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(new Color(240, 245, 255));

        JButton updateBtn = new JButton("Update Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");

        LoginUI.styleButton(updateBtn, new Color(30, 90, 160));
        LoginUI.styleButton(deleteBtn, new Color(170, 50, 50)); // Red for delete
        LoginUI.styleButton(refreshBtn, new Color(70, 70, 70));

        bottom.add(updateBtn);
        bottom.add(deleteBtn);
        bottom.add(refreshBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        add(panel);

        // ── Action Listeners ──────────────────────────────────────

        // Search Logic
        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            handleSearch(query);
        });

        // Refresh Logic
        refreshBtn.addActionListener(e -> loadData());

        // Delete Logic with RBAC check
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                handleDelete(id);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a patient to delete.");
            }
        });

        // Update Logic
        updateBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String name = (String) tableModel.getValueAt(selectedRow, 1);
                int age = (int) tableModel.getValueAt(selectedRow, 2);
                String phone = (String) tableModel.getValueAt(selectedRow, 3);
                // Open Update UI (Passing data to pre-fill)
                new UpdatePatientUI(id, name, age, phone, this).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a patient to update.");
            }
        });
    }

    private void handleSearch(String query) {
        try {
            tableModel.setRowCount(0);
            List<Patient> results = service.searchPatients(query);
            for (Patient p : results) {
                tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getAge(), p.getPhone()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage());
        }
    }

    private void handleDelete(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete Patient ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (service.deletePatient(id, userRole)) {
                    JOptionPane.showMessageDialog(this, "Patient deleted successfully.");
                    loadData();
                }
            } catch (Exception ex) {
                // If STAFF tries to delete, this catches the Access Denied exception from Service layer
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Permission Denied", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Patient> patients = service.getAllPatients();
            for (Patient p : patients) {
                tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getAge(), p.getPhone()});
            }
        } catch (Exception e) {
            LogUtil.log("ERROR: failed to load patients — " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Could not load data.");
        }
    }
}