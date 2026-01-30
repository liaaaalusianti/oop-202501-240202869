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