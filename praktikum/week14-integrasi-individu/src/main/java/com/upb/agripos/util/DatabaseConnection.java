package com.upb.agripos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility singleton to provide JDBC connections.
 * Uses simple DriverManager-based connection for local development.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/agripos";
    private static final String USER = "root";
    private static final String PASS = "";

    private static DatabaseConnection instance;

    private DatabaseConnection() {
        try {
            // Ensure driver is loaded (optional for modern drivers)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // ignore - driver might be provided by the runtime
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}