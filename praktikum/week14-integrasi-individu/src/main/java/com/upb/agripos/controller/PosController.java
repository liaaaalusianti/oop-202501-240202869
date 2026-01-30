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