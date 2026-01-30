package com.upb.agripos.model;

public class Product {
    private final String code;
    private final String name;
    private double price;
    private int stock;

    public Product(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Product(String code, String name, double price, int stock) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getCode() { 
        return code; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    public int getStock() { 
        return stock; 
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
}