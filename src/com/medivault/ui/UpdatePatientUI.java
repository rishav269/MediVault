package com.medivault.ui;



import com.medivault.service.PatientService;
import com.medivault.util.LogUtil;
import javax.swing.*;
import java.awt.*;

public class UpdatePatientUI extends JFrame {

    private final PatientService service = new PatientService();
    private final int patientId;
    private final ViewPatientUI parent;

    private JTextField nameField, ageField, phoneField;

    // The constructor that matches your error line
    public UpdatePatientUI(int id, String name, int age, String phone, ViewPatientUI parent) {
        this.patientId = id;
        this.parent = parent;
        buildUI(name, age, phone);
    }

    private void buildUI(String name, int age, String phone) {
        setTitle("Update Patient #" + patientId);
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Pre-fill the fields with existing data
        nameField = new JTextField(name);
        ageField = new JTextField(String.valueOf(age));
        phoneField = new JTextField(phone);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        JButton updateBtn = new JButton("Save Changes");
        LoginUI.styleButton(updateBtn, new Color(30, 90, 160));
        panel.add(updateBtn);

        add(panel);

        updateBtn.addActionListener(e -> handleUpdate());
    }

    private void handleUpdate() {
        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String phone = phoneField.getText().trim();

            // Call the service method we added earlier
            if (service.updatePatient(patientId, name, age, phone)) {
                JOptionPane.showMessageDialog(this, "Patient updated successfully!");
                parent.loadData(); // Refresh the table in the background
                dispose();         // Close this window
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
        }
    }
}
