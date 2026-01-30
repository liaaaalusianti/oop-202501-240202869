package com.upb.agripos.view;

import com.upb.agripos.controller.PosController;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PosView {
    private PosController controller;
    private TableView<Product> productTable;
    private TableView<CartItem> cartTable;
    private Label totalLabel;
    
    // Constructor hanya butuh 1 parameter
    public PosView(PosController controller) {
        this.controller = controller;
    }
    
    // Method createScene yang dibutuhkan AppJavaFx
    public Scene createScene(Stage stage) {
        // Layout utama
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Header
        VBox header = createHeader();
        root.setTop(header);
        
        // Main content: Product dan Cart
        SplitPane mainContent = new SplitPane();
        mainContent.setDividerPosition(0, 0.6);
        mainContent.setPrefHeight(500);
        
        // Left: Product List
        VBox productSection = createProductSection();
        mainContent.getItems().add(productSection);
        
        // Right: Shopping Cart
        VBox cartSection = createCartSection();
        mainContent.getItems().add(cartSection);
        
        root.setCenter(mainContent);
        
        // Bottom: Checkout Button
        HBox footer = createFooter();
        root.setBottom(footer);
        
        // Return Scene
        return new Scene(root, 1200, 700);
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #34495e; -fx-border-width: 0 0 2 0;");
        
        Label titleLabel = new Label("🌾 Agri-POS - Point of Sale System");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label subtitleLabel = new Label("Agricultural Product Management System");
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #bdc3c7;");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }
    
    private VBox createProductSection() {
        VBox productSection = new VBox(10);
        productSection.setPadding(new Insets(10));
        
        Label productLabel = new Label("📦 Available Products");
        productLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create Product Table
        productTable = new TableView<>();
        productTable.setItems(controller.getProductList());
        
        // Columns
        TableColumn<Product, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeCol.setPrefWidth(80);
        
        TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);
        
        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockCol.setPrefWidth(80);
        
        TableColumn<Product, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(col -> new TableCell<Product, Void>() {
            private final Button addBtn = new Button("Add to Cart");
            
            {
                addBtn.setStyle("-fx-padding: 5px 10px; -fx-font-size: 11px;");
                addBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    if (product.getStock() > 0) {
                        try {
                            controller.addToCart(product, 1);
                        } catch (Exception ex) {
                            showError("Error", "Failed to add item to cart: " + ex.getMessage());
                        }
                    } else {
                        showError("Out of Stock", "This product is out of stock");
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : addBtn);
            }
        });
        
        productTable.getColumns().addAll(codeCol, nameCol, priceCol, stockCol, actionCol);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        productSection.getChildren().addAll(productLabel, productTable);
        VBox.setVgrow(productTable, Priority.ALWAYS);
        
        return productSection;
    }
    
    private VBox createCartSection() {
        VBox cartSection = new VBox(10);
        cartSection.setPadding(new Insets(10));
        cartSection.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 0 0 0 1;");
        
        Label cartLabel = new Label("🛒 Shopping Cart");
        cartLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create Cart Table
        cartTable = new TableView<>();
        cartTable.setItems(controller.getCartItems());
        
        // Add listener untuk update total ketika cart items berubah
        controller.getCartItems().addListener((ListChangeListener<CartItem>) change -> updateTotal());
        
        // Columns
        TableColumn<CartItem, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        codeCol.setPrefWidth(70);
        
        TableColumn<CartItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        nameCol.setPrefWidth(120);
        
        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(50);
        
        TableColumn<CartItem, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceCol.setPrefWidth(80);
        
        TableColumn<CartItem, Double> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        subtotalCol.setPrefWidth(80);
        
        TableColumn<CartItem, Void> removeCol = new TableColumn<>("Remove");
        removeCol.setPrefWidth(80);
        removeCol.setCellFactory(col -> new TableCell<CartItem, Void>() {
            private final Button removeBtn = new Button("Remove");
            
            {
                removeBtn.setStyle("-fx-padding: 5px 10px; -fx-font-size: 11px; -fx-text-fill: white; -fx-background-color: #e74c3c;");
                removeBtn.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    try {
                        controller.removeFromCart(item.getProductCode());
                    } catch (Exception ex) {
                        showError("Error", "Failed to remove item: " + ex.getMessage());
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeBtn);
            }
        });
        
        cartTable.getColumns().addAll(codeCol, nameCol, qtyCol, priceCol, subtotalCol, removeCol);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Total area
        HBox totalBox = new HBox(10);
        totalBox.setPadding(new Insets(10));
        totalBox.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label totalTextLabel = new Label("Total:");
        totalTextLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        totalLabel = new Label("Rp. 0");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        
        totalBox.getChildren().addAll(totalTextLabel, totalLabel);
        
        cartSection.getChildren().addAll(cartLabel, cartTable, totalBox);
        VBox.setVgrow(cartTable, Priority.ALWAYS);
        
        return cartSection;
    }
    
    private HBox createFooter() {
        HBox footer = new HBox(10);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        footer.setAlignment(Pos.CENTER_RIGHT);
        
        Button clearBtn = new Button("🗑️ Clear Cart");
        clearBtn.setStyle("-fx-padding: 10px 20px; -fx-font-size: 12px; -fx-background-color: #95a5a6;");
        clearBtn.setOnAction(e -> {
            controller.clearCart();
            updateTotal();
        });
        
        Button checkoutBtn = new Button("✓ Checkout");
        checkoutBtn.setStyle("-fx-padding: 10px 30px; -fx-font-size: 14px; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        checkoutBtn.setOnAction(e -> {
            if (controller.getCartItems().isEmpty()) {
                showError("Empty Cart", "Please add items to cart first");
                return;
            }
            var summary = controller.checkout();
            showInfo("Checkout Success", 
                "Items: " + summary.getTotalQuantity() + "\n" +
                "Total: Rp. " + String.format("%,.0f", summary.getTotal()));
        });
        
        footer.getChildren().addAll(clearBtn, checkoutBtn);
        
        return footer;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void updateTotal() {
        double total = controller.getCartTotal();
        totalLabel.setText("Rp. " + String.format("%,.0f", total));
    }
}