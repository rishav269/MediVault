package com.medivault.ui;

import com.medivault.service.PatientService;
import com.medivault.util.LogUtil;
import javax.swing.*;
import java.awt.*;

public class UpdatePatientUI extends JFrame {

    private final PatientService service = new PatientService();
    private final int patientId;
    private final ViewPatientUI parent;

    private JTextField nameField;
    private JTextField ageField;
    private JTextField contactField;
    private JTextField diseaseField;
    private JComboBox<String> doctorDropdown;
    private JComboBox<String> statusDropdown;

    public UpdatePatientUI(int id, String name, int age, String contact, String disease, String doctor, String status, ViewPatientUI parent) {
        this.patientId = id;
        this.parent = parent;
        buildUI(name, age, contact, disease, doctor, status);
    }

    private void buildUI(String name, int age, String contact, String disease, String doctor, String status) {
        setTitle("Update Patient Medical File — ID #" + patientId);
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel headerLabel = new JLabel("Modify Patient Clinical Profile");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerLabel.setForeground(new Color(40, 50, 75));
        gbc.gridwidth = 2;
        formPanel.add(headerLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 1; gbc.gridx = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        nameField = new JTextField(name);
        nameField.setPreferredSize(new Dimension(0, 32));
        gbc.gridx = 1; formPanel.add(nameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        formPanel.add(new JLabel("Age:"), gbc);
        ageField = new JTextField(String.valueOf(age));
        ageField.setPreferredSize(new Dimension(0, 32));
        gbc.gridx = 1; formPanel.add(ageField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        formPanel.add(new JLabel("Contact No:"), gbc);
        contactField = new JTextField(contact);
        contactField.setPreferredSize(new Dimension(0, 32));
        gbc.gridx = 1; formPanel.add(contactField, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        formPanel.add(new JLabel("Diagnosis / Disease:"), gbc);
        diseaseField = new JTextField(disease);
        diseaseField.setPreferredSize(new Dimension(0, 32));
        gbc.gridx = 1; formPanel.add(diseaseField, gbc);

        gbc.gridy = 5; gbc.gridx = 0;
        formPanel.add(new JLabel("Assigned Doctor:"), gbc);
        doctorDropdown = new JComboBox<>(new String[]{"Dr. Arsh Sharma", "Dr. Shalini Katoch", "Dr. Rahul Verma", "Dr. Neha Thakur"});
        doctorDropdown.setSelectedItem(doctor);
        doctorDropdown.setPreferredSize(new Dimension(0, 32));
        gbc.gridx = 1; formPanel.add(doctorDropdown, gbc);

        gbc.gridy = 6; gbc.gridx = 0;
        formPanel.add(new JLabel("Triage Status:"), gbc);
        statusDropdown = new JComboBox<>(new String[]{"Stable", "Observation", "Recovering", "Critical"});
        statusDropdown.setSelectedItem(status);
        statusDropdown.setPreferredSize(new Dimension(0, 32));
        gbc.gridx = 1; formPanel.add(statusDropdown, gbc);

        JButton updateBtn = new JButton("Save Clinical Changes");
        LoginUI.styleButton(updateBtn, new Color(30, 90, 160));

        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        formPanel.add(updateBtn, gbc);

        add(formPanel);
        updateBtn.addActionListener(e -> handleUpdate());
    }

    private void handleUpdate() {
        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String contact = contactField.getText().trim();
            String disease = diseaseField.getText().trim();
            String doctor = (String) doctorDropdown.getSelectedItem();
            String status = (String) statusDropdown.getSelectedItem();

            LogUtil.log("Submitting record modifications pipeline target targeting ID #" + patientId);

            if (service.updatePatient(patientId, name, age, contact, disease, doctor, status)) {
                JOptionPane.showMessageDialog(this, "Patient file tracking logs updated successfully!");
                if (parent != null) {
                    parent.loadData();
                }
                dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format Error: Patient age must remain a valid numerical value.", "Validation Failure", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            LogUtil.log("ERROR: Update transaction process crashed inside window module — " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Update transaction failed: " + ex.getMessage(), "Runtime Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
}