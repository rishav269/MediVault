package com.medivault.dao;

import com.medivault.db.DBConnection;
import com.medivault.model.Patient;
import com.medivault.util.LogUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PatientDAO.java — Data Access Object (DAO Layer)
 */
public class PatientDAO {

    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (name, age, contact, disease, assigned_doctor, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LogUtil.log("ERROR: addPatient — no DB connection available.");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patient.getName());
                stmt.setInt(2, patient.getAge());
                stmt.setString(3, patient.getContact());
                stmt.setString(4, patient.getDisease());
                stmt.setString(5, patient.getAssignedDoctor());
                stmt.setString(6, patient.getStatus() != null ? patient.getStatus() : "Stable");

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    LogUtil.log("Patient added successfully: " + patient.getName());
                    return true;
                }
            }
        } catch (SQLException e) {
            LogUtil.log("ERROR: addPatient SQL failure — " + e.getMessage());
        }
        return false;
    }

    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT id, name, age, contact, disease, assigned_doctor, status FROM patients ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                LogUtil.log("ERROR: getAllPatients — no DB connection available.");
                return list;
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Patient p = new Patient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("contact"),
                            rs.getString("disease"),
                            rs.getString("assigned_doctor"),
                            rs.getString("status")
                    );
                    list.add(p);
                }
            }
            LogUtil.log("Fetched " + list.size() + " patient records from MySQL.");
        } catch (SQLException e) {
            LogUtil.log("ERROR: getAllPatients SQL failure — " + e.getMessage());
        }
        return list;
    }

    public boolean updatePatient(Patient p) throws SQLException {
        String sql = "UPDATE patients SET name=?, age=?, contact=?, disease=?, assigned_doctor=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getAge());
            ps.setString(3, p.getContact());
            ps.setString(4, p.getDisease());
            ps.setString(5, p.getAssignedDoctor());
            ps.setString(6, p.getStatus());
            ps.setInt(7, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePatient(int id) throws SQLException {
        String sql = "DELETE FROM patients WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Patient> searchPatients(String keyword) throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT id, name, age, contact, disease, assigned_doctor, status FROM patients WHERE name LIKE ? OR disease LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String wildCard = "%" + keyword.trim() + "%";
            ps.setString(1, wildCard);
            ps.setString(2, wildCard);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Patient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("contact"),
                            rs.getString("disease"),
                            rs.getString("assigned_doctor"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }
    /**
     * Gets the total count of patients in the database.
     */
    public int getTotalPatientCount() {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LogUtil.log("ERROR: Live counting patients failed — " + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets the total count of unique active doctors assigned to patients.
     */
    public int getActiveDoctorsCount() {
        String sql = "SELECT COUNT(DISTINCT assigned_doctor) FROM patients WHERE assigned_doctor != '-- Select Doctor --'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LogUtil.log("ERROR: Live counting doctors failed — " + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets the total count of pending appointments using your exact status check logic.
     */
    public int getPendingBookingsCount() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE UPPER(status) = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            LogUtil.log("ERROR: Live counting appointments failed — " + e.getMessage());
        }
        return 0;
    }

    /**
     * Fetches live rows matching your exact appointments table schema layout.
     */
    public List<Object[]> getUpcomingAppointmentsQueue() {
        List<Object[]> queueList = new ArrayList<>();
        // Query fields matching your exact image layout columns!
        String sql = "SELECT id, patient_username, doctor_name, appointment_day, appointment_time, status FROM appointments ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Combine day and time columns neatly for the UI layout display
                String scheduledSlot = rs.getString("appointment_day") + " (" + rs.getString("appointment_time") + ")";

                Object[] row = new Object[] {
                        "APT-" + rs.getInt("id"),
                        rs.getString("patient_username"), // Maps your patient username string column
                        rs.getString("doctor_name"),
                        scheduledSlot,
                        "Routine",                         // Priority/Urgency layout string placeholder
                        rs.getString("status")
                };
                queueList.add(row);
            }
        } catch (SQLException e) {
            LogUtil.log("ERROR: Loading dashboard database queue failed — " + e.getMessage());
        }
        return queueList;
    }
}