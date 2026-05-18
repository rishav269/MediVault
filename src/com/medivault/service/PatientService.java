package com.medivault.service;

import com.medivault.dao.PatientDAO;
import com.medivault.exception.InvalidInputException;
import com.medivault.model.Patient;
import com.medivault.util.LogUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * PatientService.java — Service Layer
 */
public class PatientService {

    private final PatientDAO patientDAO = new PatientDAO();

    public boolean addPatient(String name, int age, String contact, String disease, String assignedDoctor) {
        validatePatientData(name, age, contact, disease, assignedDoctor);
        Patient patient = new Patient(name.trim(), age, contact.trim(), disease.trim(), assignedDoctor.trim());
        return patientDAO.addPatient(patient);
    }

    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    public boolean deletePatient(int id, String currentUserRole) {
        if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
            LogUtil.log("Access Denied: Role '" + currentUserRole + "' tried to delete ID " + id);
            throw new InvalidInputException("Access Denied: Only Admins can delete records.");
        }
        try {
            return patientDAO.deletePatient(id);
        } catch (SQLException e) {
            LogUtil.log("Database Error during delete: " + e.getMessage());
            throw new RuntimeException("Could not delete patient record.");
        }
    }

    public boolean updatePatient(int id, String name, int age, String contact, String disease, String assignedDoctor, String status) {
        validatePatientData(name, age, contact, disease, assignedDoctor);
        Patient patient = new Patient(id, name.trim(), age, contact.trim(), disease.trim(), assignedDoctor.trim(), status.trim());
        try {
            return patientDAO.updatePatient(patient);
        } catch (SQLException e) {
            LogUtil.log("Database Error during update: " + e.getMessage());
            throw new RuntimeException("Could not update patient details.");
        }
    }

    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPatients();
        }
        try {
            return patientDAO.searchPatients(query.trim());
        } catch (SQLException e) {
            LogUtil.log("Database Error during search: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void validatePatientData(String name, int age, String contact, String disease, String assignedDoctor) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Patient name cannot be empty.");
        }
        if (age < 1 || age > 150) {
            throw new InvalidInputException("Age must be between 1 and 150.");
        }
        if (contact == null || contact.trim().isEmpty() || contact.trim().length() > 15) {
            throw new InvalidInputException("Invalid contact number format.");
        }
        if (disease == null || disease.trim().isEmpty()) {
            throw new InvalidInputException("Primary diagnosis description field is required.");
        }
        if (assignedDoctor == null || assignedDoctor.equals("-- Select Doctor --")) {
            throw new InvalidInputException("A valid professional physician must be assigned.");
        }
    }
}