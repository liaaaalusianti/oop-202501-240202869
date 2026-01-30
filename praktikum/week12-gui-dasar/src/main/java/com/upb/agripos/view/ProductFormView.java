package com.upb.agripos.view;

import com.upb.agripos.controller.ProductController;
import com.upb.agripos.model.Product;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * ProductFormView - View Layer dengan JavaFX
 * Menampilkan UI form produk dan daftar produk
 */
public class ProductFormView extends BorderPane {
    private ProductController controller;
    
    // Form components
    private TextField tfCode;
    private TextField tfName;
    private TextField tfPrice;
    private TextField tfStock;
    private ListView<String> lvProducts;
    
    public ProductFormView(ProductController controller) {
        this.controller = controller;
        initUI();
        refreshProductList();
    }
    
    private void initUI() {
        // Top section - Form
        VBox formBox = createFormBox();
        this.setTop(formBox);
        
        // Center section - Product List
        VBox listBox = createListBox();
        this.setCenter(listBox);
        
        this.setPadding(new Insets(10));
    }
    
    private VBox createFormBox() {
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10;");
        
        // Code field
        HBox codeBox = new HBox(5);
        Label lblCode = new Label("Kode Produk:");
        lblCode.setPrefWidth(100);
        tfCode = new TextField();
        tfCode.setPrefWidth(200);
        codeBox.getChildren().addAll(lblCode, tfCode);
        
        // Name field
        HBox nameBox = new HBox(5);
        Label lblName = new Label("Nama Produk:");
        lblName.setPrefWidth(100);
        tfName = new TextField();
        tfName.setPrefWidth(200);
        nameBox.getChildren().addAll(lblName, tfName);
        
        // Price field
        HBox priceBox = new HBox(5);
        Label lblPrice = new Label("Harga:");
        lblPrice.setPrefWidth(100);
        tfPrice = new TextField();
        tfPrice.setPrefWidth(200);
        priceBox.getChildren().addAll(lblPrice, tfPrice);
        
        // Stock field
        HBox stockBox = new HBox(5);
        Label lblStock = new Label("Stok:");
        lblStock.setPrefWidth(100);
        tfStock = new TextField();
        tfStock.setPrefWidth(200);
        stockBox.getChildren().addAll(lblStock, tfStock);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button btnAdd = new Button("Tambah");
        btnAdd.setPrefWidth(100);
        btnAdd.setOnAction(event -> handleAddProduct());
        
        Button btnClear = new Button("Bersihkan");
        btnClear.setPrefWidth(100);
        btnClear.setOnAction(event -> clearForm());
        
        buttonBox.getChildren().addAll(btnAdd, btnClear);
        
        formBox.getChildren().addAll(
            new Label("=== Form Input Produk ==="),
            codeBox, nameBox, priceBox, stockBox,
            buttonBox
        );
        
        return formBox;
    }
    
    private VBox createListBox() {
        VBox listBox = new VBox(10);
        listBox.setPadding(new Insets(10, 0, 0, 0));
        
        Label lblTitle = new Label("=== Daftar Produk ===");
        
        lvProducts = new ListView<>();
        lvProducts.setPrefHeight(300);
        
        HBox actionBox = new HBox(10);
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(event -> refreshProductList());
        
        Button btnDelete = new Button("Hapus");
        btnDelete.setOnAction(event -> handleDeleteProduct());
        
        actionBox.getChildren().addAll(btnRefresh, btnDelete);
        
        listBox.getChildren().addAll(lblTitle, lvProducts, actionBox);
        return listBox;
    }
    
    private void handleAddProduct() {
        try {
            String code = tfCode.getText().trim();
            String name = tfName.getText().trim();
            String price = tfPrice.getText().trim();
            String stock = tfStock.getText().trim();
            
            String result = controller.addProduct(code, name, price, stock);
            
            if (result.contains("berhasil")) {
                showAlert("Sukses", result);
                clearForm();
                refreshProductList();
            } else {
                showAlert("Gagal", result);
            }
        } catch (Exception e) {
            showAlert("Error", "Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private void handleDeleteProduct() {
        int selectedIdx = lvProducts.getSelectionModel().getSelectedIndex();
        if (selectedIdx >= 0) {
            try {
                List<Product> products = controller.getProductList();
                Product selectedProduct = products.get(selectedIdx);
                String result = controller.deleteProduct(selectedProduct.getCode());
                
                if (result.contains("berhasil")) {
                    showAlert("Sukses", result);
                    refreshProductList();
                } else {
                    showAlert("Gagal", result);
                }
            } catch (Exception e) {
                showAlert("Error", "Gagal menghapus produk: " + e.getMessage());
            }
        } else {
            showAlert("Peringatan", "Pilih produk yang akan dihapus!");
        }
    }
    
    private void refreshProductList() {
        try {
            List<Product> products = controller.getProductList();
            lvProducts.getItems().clear();
            for (Product p : products) {
                lvProducts.getItems().add(
                    String.format("%s | %s | Rp%.0f | Stok: %d", 
                        p.getCode(), p.getName(), p.getPrice(), p.getStock())
                );
            }
        } catch (Exception e) {
            showAlert("Error", "Gagal memuat data produk: " + e.getMessage());
        }
    }
    
    private void clearForm() {
        tfCode.clear();
        tfName.clear();
        tfPrice.clear();
        tfStock.clear();
        tfCode.requestFocus();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
