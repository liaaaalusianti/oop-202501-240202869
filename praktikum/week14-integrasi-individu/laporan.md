# Laporan Praktikum Minggu 14 
Topik: [Integrasi Individu (OOP + Database + GUI)]

## Identitas
- Nama  : [Lia Lusianti]
- NIM   : [240202869]
- Kelas : [3IKRB]

---

## Tujuan
- Mengintegrasikan konsep OOP (Bab 1–5) ke dalam satu aplikasi yang utuh.
- Mengimplementasikan rancangan UML + SOLID (Bab 6) menjadi kode nyata.
- Mengintegrasikan Collections + Keranjang (Bab 7) ke alur aplikasi.
- Menerapkan exception handling (Bab 9) untuk validasi dan error flow.
- Menerapkan pattern + unit testing (Bab 10) pada bagian yang relevan.
- Menghubungkan aplikasi dengan database via DAO + JDBC (Bab 11).
- Menyajikan aplikasi berbasis JavaFX (Bab 12–13) yang terhubung ke backend.

---

## Kode Program
PostCobtroller 
```java
package com.upb.agripos.controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.ProductService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * POS Controller (non-view-coupled)
 * Provides a clean API for the View to call and keeps business logic in services.
 */
public class PosController {
    private static final Logger LOGGER = Logger.getLogger(PosController.class.getName());

    private final ProductService productService;
    private final CartService cartService;
    private final ObservableList<Product> productList;
    private final ObservableList<CartItem> cartItems;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id","ID"));

    public PosController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
        this.productList = FXCollections.observableArrayList();
        this.cartItems = FXCollections.observableArrayList();
        refreshCartItems();
    }

    public void loadProducts() {
        try {
            List<Product> products = productService.findAll();
            Platform.runLater(() -> productList.setAll(products));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load products", e);
            throw new RuntimeException(e);
        }
    }

    public Product addProduct(String code, String name, double price, int stock) throws Exception {
        if (code == null || code.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product code and name must be provided");
        }
        if (price <= 0) throw new IllegalArgumentException("Price must be > 0");
        if (stock < 0) throw new IllegalArgumentException("Stock must be >= 0");

        Product product = new Product(code, name, price, stock);
        productService.insert(product);
        loadProducts();
        return product;
    }

    public void deleteProduct(String code) throws Exception {
        productService.delete(code);
        loadProducts();
    }

    public void addToCart(Product product, int quantity) throws Exception {
        cartService.addItem(product, quantity);
        refreshCartItems();
    }

    public void removeFromCart(String productCode) throws Exception {
        cartService.removeItem(productCode);
        refreshCartItems();
    }

    public void clearCart() {
        cartService.clearCart();
        refreshCartItems();
    }

    public com.upb.agripos.model.CheckoutSummary checkout() {
        com.upb.agripos.model.CheckoutSummary summary = cartService.checkout();
        // after checkout, ensure view is updated
        refreshCartItems();
        return summary;
    }

    public ObservableList<Product> getProductList() {
        return productList;
    }

    public ObservableList<CartItem> getCartItems() {
        return cartItems;
    }

    public double getCartTotal() {
        return cartService.calculateTotal();
    }

    public int getCartItemCount() {
        return cartService.getCartItemCount();
    }

    private void refreshCartItems() {
        Platform.runLater(() -> {
            cartItems.setAll(cartService.getCartItems());
        });
    }

    private String formatCurrency(double value) {
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        return currencyFormat.format(value);
    }

    /**
     * Generate a formatted receipt string using a snapshot of cart items, then perform checkout (which clears the cart).
     */
    public String generateReceipt(String cashierName) {
        if (cashierName == null || cashierName.isBlank()) cashierName = "Kasir";
        List<CartItem> itemsSnapshot = cartService.getCartItems();
        double subtotal = itemsSnapshot.stream().mapToDouble(CartItem::getSubtotal).sum();
        double tax = subtotal * 0.10;
        double total = subtotal + tax;
        int distinct = itemsSnapshot.size();
        int totalQty = itemsSnapshot.stream().mapToInt(CartItem::getQuantity).sum();

        // perform checkout to clear cart and get official summary
        com.upb.agripos.model.CheckoutSummary summary = cartService.checkout();

        StringBuilder sb = new StringBuilder();
        sb.append("-------- AGRI-POS RECEIPT --------\n");
        sb.append("Nama Kasir: ").append(cashierName).append("\n\n");
        for (CartItem it : itemsSnapshot) {
            sb.append(String.format("%s x%d = %s\n",
                    it.getProduct().getName(),
                    it.getQuantity(),
                    formatCurrency(it.getSubtotal())));
        }
        sb.append("\n");
        sb.append("Subtotal: ").append(formatCurrency(subtotal)).append("\n");
        sb.append("Pajak (10%): ").append(formatCurrency(tax)).append("\n");
        sb.append("TOTAL: ").append(formatCurrency(total)).append("\n");
        sb.append("-------------------------------");

        // Refresh the UI-observable cart items for any listeners
        refreshCartItems();
        return sb.toString();
    }

    public void printReceipt(String cashierName) {
        String receipt = generateReceipt(cashierName);
        System.out.println(receipt);
    }

    /***
     * Build a receipt string from current cart items WITHOUT performing checkout (preview-only).
     */
    public String previewReceipt(String cashierName) {
        if (cashierName == null || cashierName.isBlank()) cashierName = "Kasir";
        List<CartItem> itemsSnapshot = cartService.getCartItems();
        double subtotal = itemsSnapshot.stream().mapToDouble(CartItem::getSubtotal).sum();
        double tax = subtotal * 0.10;
        double total = subtotal + tax;

        StringBuilder sb = new StringBuilder();
        sb.append("-------- AGRI-POS RECEIPT (PREVIEW) --------\n");
        sb.append("Nama Kasir: ").append(cashierName).append("\n\n");
        for (CartItem it : itemsSnapshot) {
            sb.append(String.format("%s x%d = %s\n",
                    it.getProduct().getName(),
                    it.getQuantity(),
                    formatCurrency(it.getSubtotal())));
        }
        sb.append("\n");
        sb.append("Subtotal: ").append(formatCurrency(subtotal)).append("\n");
        sb.append("Pajak (10%): ").append(formatCurrency(tax)).append("\n");
        sb.append("TOTAL: ").append(formatCurrency(total)).append("\n");
        sb.append("----------------------------------------------");
        return sb.toString();
    }

    /**
     * Print receipt preview to terminal without clearing the cart
     */
    public void printPreviewReceipt(String cashierName) {
        String receipt = previewReceipt(cashierName);
        System.out.println(receipt);
    }
}
```
JdbcProductDAO
```java
package com.upb.agripos.dao;

import com.upb.agripos.model.Product;
import com.upb.agripos.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementasi JDBC untuk ProductDAO (Bab 11)
 */
public class JdbcProductDAO implements ProductDAO {
    private static final Logger LOGGER = Logger.getLogger(JdbcProductDAO.class.getName());
    
    @Override
    public void insert(Product product) throws Exception {
        if (product == null) throw new IllegalArgumentException("Product must not be null");
        if (product.getCode() == null || product.getCode().isBlank()) throw new IllegalArgumentException("Product code required");
        if (product.getPrice() <= 0) throw new IllegalArgumentException("Price must be > 0");
        if (product.getStock() < 0) throw new IllegalArgumentException("Stock must be >= 0");

        String sql = "INSERT INTO products (code, name, price, stock) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getCode());
            stmt.setString(2, product.getName());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStock());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Insert failed, no rows affected for product code: " + product.getCode());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to insert product: " + product, e);
            throw e;
        }
    }

    @Override
    public void update(Product product) throws Exception {
        if (product == null) throw new IllegalArgumentException("Product must not be null");
        if (product.getCode() == null || product.getCode().isBlank()) throw new IllegalArgumentException("Product code required");
        if (product.getPrice() <= 0) throw new IllegalArgumentException("Price must be > 0");
        if (product.getStock() < 0) throw new IllegalArgumentException("Stock must be >= 0");

        String sql = "UPDATE products SET name=?, price=?, stock=? WHERE code=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStock());
            stmt.setString(4, product.getCode());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Update failed, no product found with code: " + product.getCode());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update product: " + product, e);
            throw e;
        }
    }

    @Override
    public void delete(String code) throws Exception {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Code required");

        String sql = "DELETE FROM products WHERE code=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Delete failed, no product found with code: " + code);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete product code: " + code, e);
            throw e;
        }
    }

    @Override
    public Product findByCode(String code) throws Exception {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Code required");
        String sql = "SELECT code, name, price, stock FROM products WHERE code=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to find product by code: " + code, e);
            throw e;
        }
    }

    @Override
    public List<Product> findAll() throws Exception {
        String sql = "SELECT code, name, price, stock FROM products ORDER BY code";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch products", e);
            throw e;
        }
        return products;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setCode(rs.getString("code"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        product.setStock(rs.getInt("stock"));
        return product;
    }
}
```
ProductDAO
```java
package com.upb.agripos.dao;

import com.upb.agripos.model.Product;
import java.util.List;

/**
 * Interface DAO untuk Product
 * Menerapkan DIP - Dependency Inversion Principle (Bab 6)
 */
public interface ProductDAO {
    void insert(Product product) throws Exception;
    void update(Product product) throws Exception;
    void delete(String code) throws Exception;
    Product findByCode(String code) throws Exception;
    List<Product> findAll() throws Exception;
}
```
DatabaseException
```java
package com.upb.agripos.exception;

public class DatabaseException extends Exception {
    
    // Constructor dengan pesan
    public DatabaseException(String message) {
        super(message);
    }
    
    // Constructor dengan pesan dan cause (exception asli)
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
```
InsufficientStockException
```java
package com.upb.agripos.exception;

public class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }
}
```
InvalidIlnputException
```java
package com.upb.agripos.exception;

/**
 * Exception yang digunakan ketika input tidak valid pada service layer.
 */
public class InvalidInputException extends Exception {

    public InvalidInputException() {
        super();
    }

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
    }
}
```
ValidationException
```java
package com.upb.agripos.exception;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
```
Cart
```java
package com.upb.agripos.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class untuk Shopping Cart
 * Menerapkan Collections (Bab 7)
 */
public class Cart {
    private Map<String, CartItem> items; // key: product code

    public Cart() {
        this.items = new HashMap<>();
    }

    public void addItem(Product product, int quantity) {
        String code = product.getCode();
        if (items.containsKey(code)) {
            CartItem existing = items.get(code);
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            items.put(code, new CartItem(product, quantity));
        }
    }

    public void removeItem(String productCode) {
        items.remove(productCode);
    }

    public void updateQuantity(String productCode, int newQuantity) {
        if (items.containsKey(productCode)) {
            items.get(productCode).setQuantity(newQuantity);
        }
    }

    public void clear() {
        items.clear();
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public double getTotal() {
        return items.values().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    public int getItemCount() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
```
CartItem
```java
package com.upb.agripos.model;

/**
 * Model class untuk item dalam keranjang
 * Menerapkan composition (Bab 7)
 */
public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    // Helper getters for TableView PropertyValueFactory
    public String getProductCode() {
        return product.getCode();
    }

    public String getProductName() {
        return product.getName();
    }

    public double getUnitPrice() {
        return product.getPrice();
    }

    @Override
    public String toString() {
        return String.format("%s x%d = Rp %.2f", 
            product.getName(), quantity, getSubtotal());
    }
}
```
CheckoutSummary
```java
package com.upb.agripos.model;

/**
 * Ringkasan checkout sederhana
 */
public class CheckoutSummary {
    private final double subtotal;
    private final double tax;
    private final double total;
    private final int distinctItemCount;
    private final int totalQuantity;

    public CheckoutSummary(double subtotal, double tax, double total, int distinctItemCount, int totalQuantity) {
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.distinctItemCount = distinctItemCount;
        this.totalQuantity = totalQuantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTax() {
        return tax;
    }

    public double getTotal() {
        return total;
    }

    public int getDistinctItemCount() {
        return distinctItemCount;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }
}
```
Product
```java
package com.upb.agripos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class untuk Product
 * Menerapkan encapsulation (Bab 2)
 */
public class Product {
    private int id;
    private String code;
    private String name;
    private BigDecimal price;
    private int stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product() {}

    public Product(String code, String name, double price, int stock) {
        this.code = code;
        this.name = name;
        this.price = BigDecimal.valueOf(price);
        this.stock = stock;
    }

    public Product(String code, String name, BigDecimal price, int stock) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price != null ? price.doubleValue() : 0.0;
    }

    public BigDecimal getPriceAsBigDecimal() {
        return price;
    }

    public void setPrice(double price) {
        this.price = BigDecimal.valueOf(price);
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (Rp %s) Stock: %d", 
            code, name, price, stock);
    }
}
```
CartService
```java
package com.upb.agripos.service;

import com.upb.agripos.model.Cart;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;
import com.upb.agripos.exception.InvalidInputException;
import java.util.List;

/**
 * Service layer untuk Cart (Business Logic)
 * Menerapkan SRP - Single Responsibility Principle (Bab 6)
 */
public class CartService {
    private final Cart cart;
    private final ProductService productService;

    public CartService() {
        this(null);
    }

    public CartService(ProductService productService) {
        this.cart = new Cart();
        this.productService = productService;
    }

    // Existing API that uses a Product object directly
    public void addItem(Product product, int quantity) throws InvalidInputException {
        if (product == null) {
            throw new InvalidInputException("Produk tidak boleh null");
        }
        if (quantity <= 0) {
            throw new InvalidInputException("Jumlah harus lebih dari 0");
        }
        if (quantity > product.getStock()) {
            throw new InvalidInputException(
                String.format("Stok tidak mencukupi. Tersedia: %d", product.getStock())
            );
        }
        cart.addItem(product, quantity);
    }

    // New API used by tests: lookup by product code via ProductService
    public void addItemToCart(String productCode, int quantity) throws Exception {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode produk tidak boleh kosong");
        }
        if (productService == null) {
            throw new IllegalStateException("ProductService belum di-inject");
        }

        Product product = productService.getProductByCode(productCode);
        if (product == null) {
            throw new Exception("Produk tidak ditemukan: " + productCode);
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih dari 0");
        }
        if (quantity > product.getStock()) {
            throw new IllegalArgumentException("Stok tidak mencukupi. Tersedia: " + product.getStock());
        }

        cart.addItem(product, quantity);
    }

    public void removeItem(String productCode) throws InvalidInputException {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new InvalidInputException("Kode produk tidak boleh kosong");
        }
        cart.removeItem(productCode);
    }

    public void removeItemFromCart(String productCode) throws InvalidInputException {
        removeItem(productCode);
    }

    public void updateQuantity(String productCode, int newQuantity) throws InvalidInputException {
        if (productCode == null || productCode.trim().isEmpty()) {
            throw new InvalidInputException("Kode produk tidak boleh kosong");
        }
        if (newQuantity <= 0) {
            throw new InvalidInputException("Jumlah harus lebih dari 0");
        }
        cart.updateQuantity(productCode, newQuantity);
    }

    public void clearCart() {
        cart.clear();
    }

    public List<CartItem> getCartItems() {
        return cart.getItems();
    }

    // New helper used by tests: total quantity across all items
    public int getCartItemCount() {
        return cart.getItems().stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    // Existing helper for distinct item count (kept for compatibility)
    public int getItemCount() {
        return cart.getItemCount();
    }

    public double getCartTotal() {
        return cart.getTotal();
    }

    public double calculateTotal() {
        return cart.getTotal();
    }

    public boolean isCartEmpty() {
        return cart.isEmpty();
    }

    /**
     * Checkout and return a summary. Tax default is 10%.
     * This clears the cart as part of the checkout.
     */
    public com.upb.agripos.model.CheckoutSummary checkout() {
        double subtotal = cart.getTotal();
        double taxRate = 0.10; // 10% tax for demo
        double tax = subtotal * taxRate;
        int distinctItems = cart.getItemCount();
        int totalQty = getCartItemCount();
        double total = subtotal + tax;
        // Clear the cart after computing the summary
        cart.clear();
        return new com.upb.agripos.model.CheckoutSummary(subtotal, tax, total, distinctItems, totalQty);
    }
}
```
ProductService
```java
package com.upb.agripos.service;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.model.Product;
import com.upb.agripos.exception.InvalidInputException;
import java.util.List;

/**
 * Service layer untuk Product (Business Logic)
 * Menerapkan SRP - Single Responsibility Principle
 */
public class ProductService {
    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void insert(Product product) throws Exception {
        validateProduct(product);
        productDAO.insert(product);
    }

    public void update(Product product) throws Exception {
        validateProduct(product);
        productDAO.update(product);
    }

    public void delete(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidInputException("Kode produk tidak boleh kosong");
        }
        productDAO.delete(code);
    }

    public Product findByCode(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidInputException("Kode produk tidak boleh kosong");
        }
        return productDAO.findByCode(code);
    }

    public List<Product> findAll() throws Exception {
        return productDAO.findAll();
    }

    /**
     * Compatibility helper used by CartService tests to lookup product by code.
     */
    public Product getProductByCode(String code) throws Exception {
        return findByCode(code);
    }

    private void validateProduct(Product product) throws InvalidInputException {
        if (product == null) {
            throw new InvalidInputException("Produk tidak boleh null");
        }
        if (product.getCode() == null || product.getCode().trim().isEmpty()) {
            throw new InvalidInputException("Kode produk tidak boleh kosong");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new InvalidInputException("Nama produk tidak boleh kosong");
        }
        if (product.getPrice() < 0) {
            throw new InvalidInputException("Harga tidak boleh negatif");
        }
        if (product.getStock() < 0) {
            throw new InvalidInputException("Stok tidak boleh negatif");
        }
    }
}
```
DatabaseConnection
```java
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
```
AppJavaFx
```java
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
```
PosView
```java
package com.upb.agripos.view;

import com.upb.agripos.controller.PosController;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PosView {
    private PosController controller;
    private TableView<Product> productTable;
    private TableView<CartItem> cartTable;
    private Label totalLabel;
    
    // Constructor hanya butuh 1 parameter
    public PosView(PosController controller) {
        this.controller = controller;
    }
    
    // Method createScene yang dibutuhkan AppJavaFx
    public Scene createScene(Stage stage) {
        // Layout utama
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Header
        VBox header = createHeader();
        root.setTop(header);
        
        // Main content: Product dan Cart
        SplitPane mainContent = new SplitPane();
        mainContent.setDividerPosition(0, 0.6);
        mainContent.setPrefHeight(500);
        
        // Left: Product List
        VBox productSection = createProductSection();
        mainContent.getItems().add(productSection);
        
        // Right: Shopping Cart
        VBox cartSection = createCartSection();
        mainContent.getItems().add(cartSection);
        
        root.setCenter(mainContent);
        
        // Bottom: Checkout Button
        HBox footer = createFooter();
        root.setBottom(footer);
        
        // Return Scene
        return new Scene(root, 1200, 700);
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #34495e; -fx-border-width: 0 0 2 0;");
        
        Label titleLabel = new Label("🌾 Agri-POS - Point of Sale System");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label subtitleLabel = new Label("Agricultural Product Management System");
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #bdc3c7;");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }
    
    private VBox createProductSection() {
        VBox productSection = new VBox(10);
        productSection.setPadding(new Insets(10));
        
        Label productLabel = new Label("📦 Available Products");
        productLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create Product Table
        productTable = new TableView<>();
        productTable.setItems(controller.getProductList());
        
        // Columns
        TableColumn<Product, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeCol.setPrefWidth(80);
        
        TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);
        
        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockCol.setPrefWidth(80);
        
        TableColumn<Product, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(col -> new TableCell<Product, Void>() {
            private final Button addBtn = new Button("Add to Cart");
            
            {
                addBtn.setStyle("-fx-padding: 5px 10px; -fx-font-size: 11px;");
                addBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    if (product.getStock() > 0) {
                        try {
                            controller.addToCart(product, 1);
                        } catch (Exception ex) {
                            showError("Error", "Failed to add item to cart: " + ex.getMessage());
                        }
                    } else {
                        showError("Out of Stock", "This product is out of stock");
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : addBtn);
            }
        });
        
        productTable.getColumns().addAll(codeCol, nameCol, priceCol, stockCol, actionCol);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        productSection.getChildren().addAll(productLabel, productTable);
        VBox.setVgrow(productTable, Priority.ALWAYS);
        
        return productSection;
    }
    
    private VBox createCartSection() {
        VBox cartSection = new VBox(10);
        cartSection.setPadding(new Insets(10));
        cartSection.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 0 0 0 1;");
        
        Label cartLabel = new Label("🛒 Shopping Cart");
        cartLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create Cart Table
        cartTable = new TableView<>();
        cartTable.setItems(controller.getCartItems());
        
        // Add listener untuk update total ketika cart items berubah
        controller.getCartItems().addListener((ListChangeListener<CartItem>) change -> updateTotal());
        
        // Columns
        TableColumn<CartItem, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        codeCol.setPrefWidth(70);
        
        TableColumn<CartItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        nameCol.setPrefWidth(120);
        
        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(50);
        
        TableColumn<CartItem, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceCol.setPrefWidth(80);
        
        TableColumn<CartItem, Double> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        subtotalCol.setPrefWidth(80);
        
        TableColumn<CartItem, Void> removeCol = new TableColumn<>("Remove");
        removeCol.setPrefWidth(80);
        removeCol.setCellFactory(col -> new TableCell<CartItem, Void>() {
            private final Button removeBtn = new Button("Remove");
            
            {
                removeBtn.setStyle("-fx-padding: 5px 10px; -fx-font-size: 11px; -fx-text-fill: white; -fx-background-color: #e74c3c;");
                removeBtn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    try {
                        controller.removeFromCart(item.getProductCode());
                    } catch (Exception ex) {
                        showError("Error", "Failed to remove item: " + ex.getMessage());
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeBtn);
            }
        });
        
        cartTable.getColumns().addAll(codeCol, nameCol, qtyCol, priceCol, subtotalCol, removeCol);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Total area
        HBox totalBox = new HBox(10);
        totalBox.setPadding(new Insets(10));
        totalBox.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label totalTextLabel = new Label("Total:");
        totalTextLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        totalLabel = new Label("Rp. 0");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        
        totalBox.getChildren().addAll(totalTextLabel, totalLabel);
        
        cartSection.getChildren().addAll(cartLabel, cartTable, totalBox);
        VBox.setVgrow(cartTable, Priority.ALWAYS);
        
        return cartSection;
    }
    
    private HBox createFooter() {
        HBox footer = new HBox(10);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        footer.setAlignment(Pos.CENTER_RIGHT);
        
        Button clearBtn = new Button("🗑️ Clear Cart");
        clearBtn.setStyle("-fx-padding: 10px 20px; -fx-font-size: 12px; -fx-background-color: #95a5a6;");
        clearBtn.setOnAction(e -> {
            controller.clearCart();
            updateTotal();
        });
        
        Button checkoutBtn = new Button("✓ Checkout");
        checkoutBtn.setStyle("-fx-padding: 10px 30px; -fx-font-size: 14px; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        checkoutBtn.setOnAction(e -> {
            if (controller.getCartItems().isEmpty()) {
                showError("Empty Cart", "Please add items to cart first");
                return;
            }
            var summary = controller.checkout();
            showInfo("Checkout Success", 
                "Items: " + summary.getTotalQuantity() + "\n" +
                "Total: Rp. " + String.format("%,.0f", summary.getTotal()));
        });
        
        footer.getChildren().addAll(clearBtn, checkoutBtn);
        
        return footer;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void updateTotal() {
        double total = controller.getCartTotal();
        totalLabel.setText("Rp. " + String.format("%,.0f", total));
    }
}
```
---

## Hasil Eksekusi

![Screenshot hasil](screenshots/Hasil_week14(1).png)
![Screenshot hasil](screenshots/Hasil_week14(2).png)

---

## Analisis
- Program mengintegrasikan OOP, Database (JDBC + DAO), dan GUI JavaFX ke dalam satu aplikasi POS (Point of Sale) yang utuh.
- Alur kerja aplikasi mengikuti arsitektur View → Controller → Service → DAO → Database, sehingga pemisahan tugas tiap layer jelas dan terstruktur.
- JavaFX digunakan sebagai antarmuka pengguna, menampilkan daftar produk, keranjang belanja, serta proses checkout secara interaktif.
- Operasi CRUD produk dikelola melalui DAO dan Service, sedangkan manajemen keranjang menggunakan Collections (HashMap dan List).
- Perbedaan dengan minggu sebelumnya adalah integrasi penuh seluruh komponen aplikasi, tidak hanya fokus pada GUI atau database saja, melainkan membentuk sistem POS yang lengkap.
- Kendala yang dihadapi meliputi error koneksi database, sinkronisasi data GUI, dan validasi input. Kendala diatasi dengan pengecekan konfigurasi JDBC, penggunaan exception handling, serta perbaikan alur event handling pada JavaFX.
---

## Kesimpulan
- Integrasi OOP, Database, dan GUI berhasil menghasilkan aplikasi Agri-POS yang terstruktur dan fungsional.
- Penerapan DAO, Service Layer, dan JavaFX meningkatkan modularitas, keterbacaan, serta kemudahan pengembangan aplikasi.
- Penggunaan Collections, exception handling, dan arsitektur berlapis membuat sistem lebih stabil, aman, dan mudah dipelihara.
- Praktikum ini memberikan pemahaman menyeluruh tentang pembangunan aplikasi Java desktop terintegrasi.
---


