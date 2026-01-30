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