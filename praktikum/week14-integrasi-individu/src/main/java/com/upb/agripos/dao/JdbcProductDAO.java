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