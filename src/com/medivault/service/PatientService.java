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

    public boolean addPatient(String name, int age, String phone) {
        validatePatientData(name, age, phone);
        Patient patient = new Patient(name.trim(), age, phone.trim());
        return patientDAO.addPatient(patient);
    }

    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    /**
     * Deletes a patient with RBAC check.
     * Wrapped in try-catch to handle SQLException from DAO.
     */
    public boolean deletePatient(int id, String currentUserRole) {
        if (!"ADMIN".equalsIgnoreCase(currentUserRole)) {
            LogUtil.log("Access Denied: Role '" + currentUserRole + "' tried to delete ID " + id);
            throw new InvalidInputException("Access Denied: Only Admins can delete records.");
        }

        try {
            boolean success = patientDAO.deletePatient(id);
            if (success) LogUtil.log("Patient ID " + id + " deleted by ADMIN.");
            return success;
        } catch (SQLException e) {
            LogUtil.log("Database Error during delete: " + e.getMessage());
            throw new RuntimeException("Could not delete patient due to database error.");
        }
    }

    /**
     * Updates patient details.
     * Handles SQLException from DAO.
     */
    public boolean updatePatient(int id, String name, int age, String phone) {
        validatePatientData(name, age, phone);

        Patient patient = new Patient(id, name.trim(), age, phone.trim());
        try {
            return patientDAO.updatePatient(patient);
        } catch (SQLException e) {
            LogUtil.log("Database Error during update: " + e.getMessage());
            throw new RuntimeException("Could not update patient details.");
        }
    }

    /**
     * Search operation.
     * Handles SQLException from DAO and returns empty list if search fails.
     */
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

    /**
     * Private helper to keep validation logic DRY (Don't Repeat Yourself).
     */
    private void validatePatientData(String name, int age, String phone) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Patient name cannot be empty.");
        }
        if (age < 1 || age > 150) {
            throw new InvalidInputException("Age must be between 1 and 150.");
        }
        if (phone == null || phone.trim().isEmpty() || phone.trim().length() > 15) {
            throw new InvalidInputException("Invalid phone number (max 15 chars).");
        }
    }
}