package com.medivault.dao;

import com.medivault.db.DBConnection;
import com.medivault.util.LogUtil;
import java.sql.*;

/**
 * UserDAO.java — Data Access Object for Users
 */
public class userDAO {

    public String validateLogin(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                LogUtil.log("ERROR: UserDAO — No database connection.");
                return null;
            }

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            LogUtil.log("ERROR: UserDAO Login Failure — " + e.getMessage());
        }
        return null;
    }
}