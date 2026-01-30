package com.upb.agripos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.upb.agripos.controller.PosController;
import com.upb.agripos.dao.JdbcProductDAO;
import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.view.PosView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main Application JavaFX untuk Agri-POS
 * Mengintegrasikan semua layer: View, Controller, Service, DAO
 * Menerapkan Dependency Injection (Bab 6 - SOLID)
 */
public class AppJavaFx extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/agripos";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    @Override
    public void start(Stage primaryStage) {
        // Bab 1: Identitas Praktikum
        System.out.println("Hello World, I am [Lia Lusianti]-[240202869]");
        System.out.println("=== Agri-POS System Started ===");

        try {
            // Initialize database and create tables if needed
            initializeDatabase();
            
            // Dependency Injection - mengikuti DIP (Dependency Inversion Principle)
            ProductDAO productDAO = new JdbcProductDAO();
            ProductService productService = new ProductService(productDAO);
            CartService cartService = new CartService();
            
            PosController controller = new PosController(productService, cartService);
            controller.loadProducts(); // Load data awal

            PosView view = new PosView(controller);
            Scene scene = view.createScene(primaryStage);

            primaryStage.setTitle("Agri-POS - Point of Sale System");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("✓ Aplikasi Agri-POS berhasil dijalankan");
        } catch (Exception e) {
            System.err.println("✗ Error saat menjalankan aplikasi:");
            e.printStackTrace();
        }
    }

    /**
     * Inisialisasi database dan buat tabel jika belum ada
     */
    private void initializeDatabase() {
        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ MySQL Driver loaded");

            // Test connection
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                System.out.println("✓ Database connected: " + DB_URL);
                
                // Create products table if not exists
                try (Statement stmt = conn.createStatement()) {
                    String createTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                            "code VARCHAR(50) PRIMARY KEY, " +
                            "name VARCHAR(255) NOT NULL, " +
                            "price DECIMAL(10,2) NOT NULL, " +
                            "stock INTEGER NOT NULL DEFAULT 0, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                    
                    stmt.execute(createTableSQL);
                    System.out.println("✓ Table 'products' ready");
                    
                    // Check if table has any data
                    String checkDataSQL = "SELECT COUNT(*) as count FROM products";
                    var rs = stmt.executeQuery(checkDataSQL);
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        System.out.println("✓ Products in database: " + count);
                        
                        // Insert sample data if table is empty
                        if (count == 0) {
                            insertSampleData(stmt);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Insert sample product data jika tabel kosong
     */
    private void insertSampleData(Statement stmt) throws SQLException {
        String[] insertQueries = {
            "INSERT INTO products (code, name, price, stock) VALUES ('BRY-001', 'Benih Padi Premium', 50000, 100)",
            "INSERT INTO products (code, name, price, stock) VALUES ('BRY-002', 'Benih Jagung Hibrida', 75000, 80)",
            "INSERT INTO products (code, name, price, stock) VALUES ('PUK-001', 'Pupuk Organik 50kg', 150000, 50)",
            "INSERT INTO products (code, name, price, stock) VALUES ('PUK-002', 'Pupuk NPK 50kg', 120000, 60)",
            "INSERT INTO products (code, name, price, stock) VALUES ('OBT-001', 'Obat Hama Organik 1L', 85000, 40)",
            "INSERT INTO products (code, name, price, stock) VALUES ('OBT-002', 'Pestisida Kimia 1L', 95000, 35)"
        };
        
        for (String sql : insertQueries) {
            try {
                stmt.execute(sql);
            } catch (SQLException e) {
                // Ignore duplicate key errors
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
        System.out.println("✓ Sample data inserted");
    }

    public static void main(String[] args) {
        launch(args);
    }
}