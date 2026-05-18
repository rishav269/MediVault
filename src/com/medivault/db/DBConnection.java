package com.medivault.db;

import com.medivault.util.LogUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/medivault";
    private static final String USER     = "root";
    private static final String PASSWORD = "rootroot";

    /**
     * Opens and returns a MySQL Connection.
     */
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            if (conn != null) {
                conn.setAutoCommit(true); // Forces immediate relational changes persistence
            }
            return conn;

        } catch (ClassNotFoundException e) {
            LogUtil.log("ERROR: MySQL JDBC driver not found — " + e.getMessage());
            System.err.println("Driver missing: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            LogUtil.log("ERROR: Database connection failed — " + e.getMessage());
            System.err.println("DB connection error: " + e.getMessage());
            return null;
        }
    }
}