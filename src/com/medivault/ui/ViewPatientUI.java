package com.medivault.ui;

import com.medivault.model.Patient;
import com.medivault.service.PatientService;
import com.medivault.util.LogUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ViewPatientUI.java — Modernized 7-Column Schema Management Ledger
 */
public class ViewPatientUI extends JFrame {

    private final PatientService service = new PatientService();
    private DefaultTableModel tableModel;
    private JTable table;
    private final String userRole;

    public ViewPatientUI(String role) {
        this.userRole = role;
        buildUI();
        loadData();
    }

    private void buildUI() {
        setTitle("MediVault Master Ledger — Session View: (" + userRole.toUpperCase() + ")");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(new Color(240, 245, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(240, 245, 255));

        JTextField searchField = new JTextField(22);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(0, 30));

        JButton searchBtn = new JButton("Filter Ledger");
        LoginUI.styleButton(searchBtn, new Color(45, 60, 85));
        searchBtn.setPreferredSize(new Dimension(120, 30));

        topPanel.add(new JLabel("Search Parameter:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Patient ID", "Full Name", "Age", "Contact No", "Diagnosis / Disease", "Assigned Doctor", "Current Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        bottom.setBackground(new Color(240, 245, 255));

        JButton updateBtn = new JButton("Modify Selection");
        JButton deleteBtn = new JButton("Purge Record");
        JButton refreshBtn = new JButton("Sync Table");

        LoginUI.styleButton(updateBtn, new Color(30, 90, 160));
        LoginUI.styleButton(deleteBtn, new Color(170, 50, 50));
        LoginUI.styleButton(refreshBtn, new Color(70, 70, 70));

        bottom.add(updateBtn);
        bottom.add(deleteBtn);
        bottom.add(refreshBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        add(panel);

        searchBtn.addActionListener(e -> handleSearch(searchField.getText().trim()));
        refreshBtn.addActionListener(e -> loadData());

        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                handleDelete(id);
            } else {
                JOptionPane.showMessageDialog(this, "Please highlight a patient entry from the list to remove.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
            }
        });

        updateBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String name = (String) tableModel.getValueAt(selectedRow, 1);
                int age = (int) tableModel.getValueAt(selectedRow, 2);
                String contact = (String) tableModel.getValueAt(selectedRow, 3);
                String disease = (String) tableModel.getValueAt(selectedRow, 4);
                String doctor = (String) tableModel.getValueAt(selectedRow, 5);
                String status = (String) tableModel.getValueAt(selectedRow, 6);

                new UpdatePatientUI(id, name, age, contact, disease, doctor, status, this).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please highlight a patient entry to update details.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void handleSearch(String query) {
        try {
            tableModel.setRowCount(0);
            List<Patient> results = service.searchPatients(query);
            for (Patient p : results) {
                tableModel.addRow(new Object[]{
                        p.getId(), p.getName(), p.getAge(), p.getContact(), p.getDisease(), p.getAssignedDoctor(), p.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Filter processing anomaly: " + ex.getMessage());
        }
    }

    private void handleDelete(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Permanently destroy record tracking ID #" + id + "?", "Confirm Drop Execution", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (service.deletePatient(id, userRole)) {
                    JOptionPane.showMessageDialog(this, "Patient files dropped cleanly from ledger registers.");
                    loadData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Governance Enforcement", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Patient> patients = service.getAllPatients();
            for (Patient p : patients) {
                tableModel.addRow(new Object[]{
                        p.getId(), p.getName(), p.getAge(), p.getContact(), p.getDisease(), p.getAssignedDoctor(), p.getStatus()
                });
            }
        } catch (Exception e) {
            LogUtil.log("ERROR: ViewPatientUI component context data refresh crash — " + e.getMessage());
            JOptionPane.showMessageDialog(this, "System layer error stalling master datagrid synchronization workflows.");
        }
    }
}