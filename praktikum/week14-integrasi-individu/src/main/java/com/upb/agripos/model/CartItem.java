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