package com.medivault.ui;

import com.medivault.exception.InvalidInputException;
import com.medivault.service.PatientService;
import com.medivault.util.LogUtil;

import javax.swing.*;
import java.awt.*;

/**
 * AddPatientUI.java — UI Layer: Add Patient Form
 *
 * Collects Name, Age, and Phone from the user.
 * Passes values to PatientService for validation and saving.
 *
 * [EXCEPTION HANDLING] used in handleSave():
 *   1. NumberFormatException  — age field contains non-numeric text (e.g. "abc")
 *   2. InvalidInputException  — service-layer validation rejected the data
 *   3. Exception (generic)    — safety net for any other unexpected error
 *
 * [FILE HANDLING] — errors and successes ultimately logged via LogUtil
 *                   (inside PatientService and PatientDAO).
 */
public class AddPatientUI extends JFrame {

    private final PatientService service = new PatientService();

    private JTextField nameField;
    private JTextField ageField;
    private JTextField phoneField;

    public AddPatientUI() {
        buildUI();
    }

    private void buildUI() {
        setTitle("MediVault — Add Patient");
        setSize(370, 270);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // don't quit the whole app
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 30, 18, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // ── Title ─────────────────────────────────────────────────
        JLabel title = new JLabel("Add New Patient", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(new Color(30, 90, 160));
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        panel.add(title, g);
        g.gridwidth = 1;

        // ── Name ──────────────────────────────────────────────────
        g.gridy = 1; g.gridx = 0; panel.add(new JLabel("Name:"),  g);
        nameField = new JTextField(15);
        g.gridx = 1; panel.add(nameField, g);

        // ── Age ───────────────────────────────────────────────────
        g.gridy = 2; g.gridx = 0; panel.add(new JLabel("Age:"),   g);
        ageField = new JTextField(15);
        g.gridx = 1; panel.add(ageField, g);

        // ── Phone ─────────────────────────────────────────────────
        g.gridy = 3; g.gridx = 0; panel.add(new JLabel("Phone:"), g);
        phoneField = new JTextField(15);
        g.gridx = 1; panel.add(phoneField, g);

        // ── Buttons ───────────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setBackground(new Color(240, 245, 255));

        JButton saveBtn  = new JButton("Save");
        JButton clearBtn = new JButton("Clear");
        LoginUI.styleButton(saveBtn,  new Color(34, 130, 34));
        LoginUI.styleButton(clearBtn, new Color(110, 110, 110));
        saveBtn.setPreferredSize(new Dimension(85, 30));
        clearBtn.setPreferredSize(new Dimension(85, 30));
        btnRow.add(saveBtn);
        btnRow.add(clearBtn);

        g.gridy = 4; g.gridx = 0; g.gridwidth = 2;
        panel.add(btnRow, g);

        add(panel);

        saveBtn.addActionListener(e  -> handleSave());
        clearBtn.addActionListener(e -> clearFields());
    }

    /**
     * Reads form values, parses age, then calls the service layer.
     *
     * [EXCEPTION HANDLING — three layers]:
     *   1. NumberFormatException: Integer.parseInt fails when age is not a number.
     *   2. InvalidInputException: service rejects blank/out-of-range values.
     *   3. Exception (generic) : unexpected runtime problem.
     */
    private void handleSave() {
        String name    = nameField.getText();
        String ageText = ageField.getText().trim();
        String phone   = phoneField.getText();

        // ── 1. Parse age — may throw NumberFormatException ────────
        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            // [EXCEPTION HANDLING] user typed letters in the age field
            LogUtil.log("Invalid input: age is not a number — '" + ageText + "'");
            JOptionPane.showMessageDialog(this,
                    "Age must be a whole number (e.g. 30).",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            ageField.requestFocus();
            return;
        }

        // ── 2. Call service — may throw InvalidInputException ──────
        try {
            boolean ok = service.addPatient(name, age, phone);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Patient added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not save patient.\nCheck the database connection.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (InvalidInputException e) {
            // [EXCEPTION HANDLING] validation error from PatientService
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);

        } catch (Exception e) {
            // [EXCEPTION HANDLING] unexpected error — log and inform user
            LogUtil.log("ERROR: unexpected error in AddPatientUI — " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        phoneField.setText("");
        nameField.requestFocus();
    }
}
