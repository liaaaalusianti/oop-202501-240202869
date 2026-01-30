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