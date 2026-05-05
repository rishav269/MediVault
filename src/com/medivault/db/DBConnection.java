package com.medivault.db;

import com.medivault.util.LogUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java — Database Connection (DB Layer)
 *
 * Provides a single static method that returns a live MySQL Connection.
 * Called by PatientDAO for every query.
 *
 * ── BEFORE RUNNING ─────────────────────────────────────────────────────────
 * 1. Make sure MySQL is running.
 * 2. Run setup.sql to create the database and patients table.
 * 3. Update USER and PASSWORD below to match your MySQL credentials.
 * 4. Add mysql-connector-j-8.x.x.jar to the compile/run classpath.
 * ───────────────────────────────────────────────────────────────────────────
 *
 * [EXCEPTION HANDLING] — ClassNotFoundException and SQLException are caught
 *                        and logged; null is returned so callers can check.
 */
public class DBConnection {

    // ── Configure these three values to match your MySQL setup ──
    private static final String URL      = "jdbc:mysql://localhost:3306/medivault";
    private static final String USER     = "root";            // your MySQL username
    private static final String PASSWORD = "rootroot";   // your MySQL password

    /**
     * Opens and returns a MySQL Connection.
     * Returns null if the connection cannot be established.
     */
    public static Connection getConnection() {
        try {
            // Load the MySQL JDBC driver (required for older JDKs / explicit registration)
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;

        } catch (ClassNotFoundException e) {
            // [EXCEPTION HANDLING] Driver JAR not on classpath
            LogUtil.log("ERROR: MySQL JDBC driver not found — " + e.getMessage());
            System.err.println("Driver missing: " + e.getMessage());
            return null;

        } catch (SQLException e) {
            // [EXCEPTION HANDLING] DB not running, wrong credentials, bad URL, etc.
            LogUtil.log("ERROR: Database connection failed — " + e.getMessage());
            System.err.println("DB connection error: " + e.getMessage());
            return null;
        }
    }
}
