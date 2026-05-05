package com.medivault.dao;


import com.medivault.db.DBConnection;
import com.medivault.util.LogUtil;
import java.sql.*;

/**
 * UserDAO.java — Data Access Object for Users
 * Handles authentication and role retrieval from the MySQL 'users' table.
 */
public class userDAO {

    /**
     * Validates credentials against the database.
     * @param username The input username
     * @param password The input password
     * @return The user's role (ADMIN or STAFF) if valid, null otherwise.
     */
    public String validateLogin(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                LogUtil.log("ERROR: UserDAO — No database connection.");
                return null;
            }

            ps.setString(1, username);
            ps.setString(2, password); // Currently plain text; matching your logic

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role"); // Returns "ADMIN" or "STAFF"
                }
            }

        } catch (SQLException e) {
            LogUtil.log("ERROR: UserDAO Login Failure — " + e.getMessage());
            System.err.println("SQL error (validateLogin): " + e.getMessage());
        }

        return null; // Login failed or error occurred
    }
}