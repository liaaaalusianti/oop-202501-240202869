package com.upb.agripos.view;

public class ConsoleView {
    public void showMessage(String message) {
        System.out.println(message);
    }
    
    public void showProduct(String code, String name, double price, int stock) {
        System.out.println("=== Detail Produk ===");
        System.out.println("Kode    : " + code);
        System.out.println("Nama    : " + name);
        System.out.println("Harga   : Rp " + price);
        System.out.println("Stok    : " + stock + " unit");
        System.out.println("====================");
    }
    
    public void showError(String errorMessage) {
        System.err.println("ERROR: " + errorMessage);
    }
}