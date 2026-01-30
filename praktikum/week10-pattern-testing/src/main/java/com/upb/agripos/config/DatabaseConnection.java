package com.upb.agripos.config;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private String connectionString;
    private boolean isConnected;

    // Constructor private - tidak bisa diakses dari luar
    private DatabaseConnection() {
        this.connectionString = "jdbc:mysql://localhost:3306/agripos";
        this.isConnected = false;
        System.out.println("DatabaseConnection instance dibuat!");
    }

    // Method static untuk mendapatkan instance
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void connect() {
        if (!isConnected) {
            isConnected = true;
            System.out.println("Berhasil terkoneksi ke database: " + connectionString);
        } else {
            System.out.println("Sudah terkoneksi ke database.");
        }
    }

    public void disconnect() {
        if (isConnected) {
            isConnected = false;
            System.out.println("Koneksi database ditutup.");
        }
    }

    public String getConnectionString() {
        return connectionString;
    }

    public boolean isConnected() {
        return isConnected;
    }
}