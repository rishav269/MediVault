package com.medivault.dao;

import com.medivault.db.DBConnection;
import com.medivault.model.Patient;
import com.medivault.util.LogUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PatientDAO.java — Data Access Object (DAO Layer)
 *
 * Contains all SQL operations for the patients table.
 * Uses PreparedStatement to prevent SQL injection.
 *
 * [EXCEPTION HANDLING] — every SQL block is wrapped in try-catch(SQLException).
 * [FILE HANDLING]      — results and errors are logged via LogUtil.
 */
public class PatientDAO {

    /**
     * Inserts a new patient into the database.
     *
     * @param patient Patient object (name, age, phone already set)
     * @return true  if INSERT succeeded
     *         false if connection is null or SQL fails
     */
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (name, age, phone) VALUES (?, ?, ?)";

        // [EXCEPTION HANDLING] try-with-resources closes Connection + Statement automatically
        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                LogUtil.log("ERROR: addPatient — no DB connection available.");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patient.getName());
                stmt.setInt(2, patient.getAge());
                stmt.setString(3, patient.getPhone());

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    // [FILE HANDLING] log successful insert
                    LogUtil.log("Patient added successfully: name=" + patient.getName()
                            + ", age=" + patient.getAge()
                            + ", phone=" + patient.getPhone());
                    return true;
                }
            }

        } catch (SQLException e) {
            // [EXCEPTION HANDLING] SQL error during INSERT
            // [FILE HANDLING]      write error details to log.txt
            LogUtil.log("ERROR: addPatient SQL failure — " + e.getMessage());
            System.err.println("SQL error (addPatient): " + e.getMessage());
        }

        return false;
    }

    /**
     * Fetches every row from the patients table.
     *
     * @return List<Patient> — empty list if table is empty or an error occurs
     */
    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY id ASC";

        // [EXCEPTION HANDLING] try-with-resources closes all JDBC objects
        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                LogUtil.log("ERROR: getAllPatients — no DB connection available.");
                return list;
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs   = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Patient p = new Patient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("phone"));
                    list.add(p);
                }
            }

            // [FILE HANDLING] log how many records were fetched
            LogUtil.log("Fetched " + list.size() + " patient(s) from database.");

        } catch (SQLException e) {
            // [EXCEPTION HANDLING] SQL error during SELECT
            LogUtil.log("ERROR: getAllPatients SQL failure — " + e.getMessage());
            System.err.println("SQL error (getAllPatients): " + e.getMessage());
        }

        return list;
    }

    public boolean updatePatient(Patient p) throws SQLException {
        String sql = "UPDATE patients SET name=?, age=?, phone=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setInt(2, p.getAge());
            ps.setString(3, p.getPhone());
            ps.setInt(4, p.getId());
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

    public List<Patient> searchPatients(String query) throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE name LIKE ? OR id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            // Handle potential non-numeric ID search
            try { ps.setInt(2, Integer.parseInt(query)); }
            catch (NumberFormatException e) { ps.setInt(2, -1); }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Patient(rs.getInt("id"), rs.getString("name"), rs.getInt("age"), rs.getString("phone")));
            }
        }
        return list;
    }
}
