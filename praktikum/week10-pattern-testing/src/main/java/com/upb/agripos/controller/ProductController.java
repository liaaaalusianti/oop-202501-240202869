package com.upb.agripos.controller;

import com.upb.agripos.model.Product;
import com.upb.agripos.view.ConsoleView;

public class ProductController {
    private final Product model;
    private final ConsoleView view;

    public ProductController(Product model, ConsoleView view) {
        this.model = model;
        this.view = view;
    }

    public void showProduct() {
        view.showMessage("Produk: " + model.getCode() + " - " + model.getName());
    }
    
    public void showProductDetails() {
        view.showProduct(
            model.getCode(), 
            model.getName(), 
            model.getPrice(), 
            model.getStock()
        );
    }
    
    public void updatePrice(double newPrice) {
        if (newPrice < 0) {
            view.showError("Harga tidak boleh negatif!");
            return;
        }
        model.setPrice(newPrice);
        view.showMessage("Harga berhasil diupdate menjadi Rp " + newPrice);
    }
    
    public void updateStock(int newStock) {
        if (newStock < 0) {
            view.showError("Stok tidak boleh negatif!");
            return;
        }
        model.setStock(newStock);
        view.showMessage("Stok berhasil diupdate menjadi " + newStock + " unit");
    }
}
