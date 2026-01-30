package com.upb.agripos;

import com.upb.agripos.config.DatabaseConnection;
import com.upb.agripos.controller.ProductController;
import com.upb.agripos.model.Product;
import com.upb.agripos.view.ConsoleView;

public class AppMVC {
    public static void main(String[] args) {
        // Ganti dengan nama dan NIM Anda
        System.out.println("Hello, I am Lia Lusianti-240202869 (Week10)");
        
        // Demo Singleton Pattern
        System.out.println("=== DEMO SINGLETON PATTERN ===");
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        
        System.out.println("db1 == db2? " + (db1 == db2)); // Harus true
        db1.connect();
        System.out.println();
        
        // Demo MVC Pattern
        System.out.println("=== DEMO MVC PATTERN ===");
        Product product = new Product("P01", "Pupuk Organik", 50000, 100);
        ConsoleView view = new ConsoleView();
        ProductController controller = new ProductController(product, view);
        
        // Tampilkan produk
        controller.showProduct();
        System.out.println();
        
        // Tampilkan detail produk
        controller.showProductDetails();
        System.out.println();
        
        // Update harga
        controller.updatePrice(55000);
        
        // Update stok
        controller.updateStock(150);
        System.out.println();
        
        // Tampilkan detail setelah update
        controller.showProductDetails();
    }
}