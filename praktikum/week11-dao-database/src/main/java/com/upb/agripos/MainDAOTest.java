package com.upb.agripos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.dao.ProductDAOImpl;
import com.upb.agripos.model.Product;

public class MainDAOTest {
    public static void main(String[] args) {
        Connection conn = null;
        
        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Koneksi ke database MySQL
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agripos",
                "root",
                ""
            );
            System.out.println("Database connected!");
            
            ProductDAO dao = new ProductDAOImpl(conn);

            // Bersihkan data lama agar tidak error duplicate key saat insert ulang
            try {
                dao.delete("P01");
            } catch (Exception e) {
                // No old data
            }

            // ========== CRUD OPERATIONS ==========
            
            // 1. INSERT
            System.out.println("Inserting product...");
            dao.insert(new Product("P01", "Pupuk Organik Premium", 100000, 20));
            
            // 2. UPDATE
            System.out.println("Updating product...");
            dao.update(new Product("P01", "Pupuk Organik Premium", 100000, 20));

            // 3. FIND BY CODE
            Product p = dao.findByCode("P01");
            if (p != null) {
                System.out.println("Found: " + p.getName() + " | Price: " + p.getPrice());
            }

            // 4. FIND ALL
            System.out.println("All Products:");
            List<Product> list = dao.findAll();
            if (!list.isEmpty()) {
                for (Product prod : list) {
                    System.out.println("- " + prod.getName() + " (" + prod.getStock() + ")");
                }
            }

            // 5. DELETE
            System.out.println("Deleting product...");
            dao.delete("P01");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL Driver not found!");
            e.printStackTrace();
            
        } catch (java.sql.SQLException e) {
            System.err.println("Error: Database connection failed!");
            e.printStackTrace();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            // Tutup koneksi
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (Exception e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        
        System.out.println("\ncredit by: 240202869 - Lia Lusianti");
    }
}