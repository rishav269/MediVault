package com.medivault.ui;

import com.medivault.service.PatientService;
import com.medivault.util.LogUtil;
import javax.swing.*;
import java.awt.*;

/**
 * AddPatientUI.java — Refactored to match the modern split-screen design.
 * Features new administrative inputs for entering a disease and assigning a doctor.
 */
public class AddPatientUI extends JFrame {

    private JTextField nameField;
    private JTextField ageField;
    private JTextField contactField;

    private JTextField diseaseField;
    private JComboBox<String> doctorDropdown;

    private final PatientService service = new PatientService();

    public AddPatientUI() {
        buildUI();
    }

    private void buildUI() {
        setTitle("MediVault — Register New Patient");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Split screen: 1 row, 2 columns layout
        setLayout(new GridLayout(1, 2));

        // ==========================================
        //  LEFT PANEL: Aesthetic Decorative Accent
        // ==========================================
        JPanel leftPanel = new JPanel(new GridBagLayout());

        // CHANGED: Swapped out the old action green for the official MediVault Dark Blue brand color
        leftPanel.setBackground(new Color(30, 90, 160));

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0; leftGbc.gridy = 0;
        leftGbc.insets = new Insets(10, 20, 10, 20);

        JLabel sideTitle = new JLabel("Patient Intake");
        sideTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
        sideTitle.setForeground(Color.WHITE);
        leftPanel.add(sideTitle, leftGbc);

        leftGbc.gridy = 1;
        JLabel sideSub = new JLabel("Administrative Admission Portal");
        sideSub.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // CHANGED: Matched text color tone dynamically to the new blue background palette
        sideSub.setForeground(new Color(200, 220, 245));
        leftPanel.add(sideSub, leftGbc);

        // ==========================================
        //  RIGHT PANEL: Clean Dual-Column Input Form
        // ==========================================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 10, 8);

        // Form Header Text
        JLabel formHeader = new JLabel("Admission Details");
        formHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        formHeader.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(formHeader, gbc);

        // Reset grid width rules for standard form rows
        gbc.gridwidth = 1;

        // Row 1: Patient Name
        gbc.gridy = 1; gbc.gridx = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        nameField = createStyledTextField();
        gbc.gridx = 1; formPanel.add(nameField, gbc);

        // Row 2: Age
        gbc.gridy = 2; gbc.gridx = 0;
        formPanel.add(new JLabel("Age:"), gbc);
        ageField = createStyledTextField();
        gbc.gridx = 1; formPanel.add(ageField, gbc);

        // Row 3: Contact Phone Number
        gbc.gridy = 3; gbc.gridx = 0;
        formPanel.add(new JLabel("Contact No:"), gbc);
        contactField = createStyledTextField();
        gbc.gridx = 1; formPanel.add(contactField, gbc);

        // Row 4: NEW DISEASE FIELD
        gbc.gridy = 4; gbc.gridx = 0;
        formPanel.add(new JLabel("Diagnosis / Disease:"), gbc);
        diseaseField = createStyledTextField();
        diseaseField.setToolTipText("Enter primary diagnosis details");
        gbc.gridx = 1; formPanel.add(diseaseField, gbc);

        // Row 5: NEW ASSIGNED DOCTOR DROPDOWN
        gbc.gridy = 5; gbc.gridx = 0;
        formPanel.add(new JLabel("Assign Practitioner:"), gbc);
        doctorDropdown = new JComboBox<>(new String[]{
                "-- Select Doctor --", "Dr. Arsh Sharma", "Dr. Shalini Katoch", "Dr. Rahul Verma", "Dr. Neha Thakur"
        });
        doctorDropdown.setPreferredSize(new Dimension(0, 32));
        doctorDropdown.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridx = 1; formPanel.add(doctorDropdown, gbc);

        // Action Buttons Row (Save and Clear)
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 15, 0));
        btnRow.setBackground(Color.WHITE);

        JButton saveBtn = new JButton("Save Record");
        LoginUI.styleButton(saveBtn, new Color(30, 90, 160)); // Swapped button color to theme blue!

        JButton clearBtn = new JButton("Clear Form");
        LoginUI.styleButton(clearBtn, new Color(120, 130, 140));

        btnRow.add(saveBtn);
        btnRow.add(clearBtn);

        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 8, 5, 8);
        formPanel.add(btnRow, gbc);

        // Assemble pieces into main window Frame
        add(leftPanel);
        add(formPanel);

        // Event Listeners
        clearBtn.addActionListener(e -> clearFormFields());
        saveBtn.addActionListener(e -> handlePatientRegistration());
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setPreferredSize(new Dimension(0, 32));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return field;
    }

    private void clearFormFields() {
        nameField.setText("");
        ageField.setText("");
        contactField.setText("");
        diseaseField.setText("");
        doctorDropdown.setSelectedIndex(0);
        nameField.requestFocus();
    }

    private void handlePatientRegistration() {
        String name = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String contact = contactField.getText().trim();
        String disease = diseaseField.getText().trim();
        String assignedDoc = (String) doctorDropdown.getSelectedItem();

        if (name.isEmpty() || ageStr.isEmpty() || contact.isEmpty() || disease.isEmpty() || doctorDropdown.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please fill in all clinical and administrative fields.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            boolean success = service.addPatient(name, age, contact, disease, assignedDoc);

            if (success) {
                LogUtil.log("Admin registered patient: " + name + " | Assigned to: " + assignedDoc);
                JOptionPane.showMessageDialog(this, "Patient clinical profile saved successfully!", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
                clearFormFields();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Database write operation failed.", "Execution Failure", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age field must contain a valid numerical value.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}