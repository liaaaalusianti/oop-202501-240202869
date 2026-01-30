package com.upb.agripos.service;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.model.Product;
import com.upb.agripos.exception.InvalidInputException;
import java.util.List;

/**
 * Service layer untuk Product (Business Logic)
 * Menerapkan SRP - Single Responsibility Principle
 */
public class ProductService {
    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void insert(Product product) throws Exception {
        validateProduct(product);
        productDAO.insert(product);
    }

    public void update(Product product) throws Exception {
        validateProduct(product);
        productDAO.update(product);
    }

    public void delete(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidInputException("Kode produk tidak boleh kosong");
        }
        productDAO.delete(code);
    }

    public Product findByCode(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            throw new InvalidInputException("Kode produk tidak boleh kosong");
        }
        return productDAO.findByCode(code);
    }

    public List<Product> findAll() throws Exception {
        return productDAO.findAll();
    }

    /**
     * Compatibility helper used by CartService tests to lookup product by code.
     */
    public Product getProductByCode(String code) throws Exception {
        return findByCode(code);
    }

    private void validateProduct(Product product) throws InvalidInputException {
        if (product == null) {
            throw new InvalidInputException("Produk tidak boleh null");
        }
        if (product.getCode() == null || product.getCode().trim().isEmpty()) {
            throw new InvalidInputException("Kode produk tidak boleh kosong");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new InvalidInputException("Nama produk tidak boleh kosong");
        }
        if (product.getPrice() < 0) {
            throw new InvalidInputException("Harga tidak boleh negatif");
        }
        if (product.getStock() < 0) {
            throw new InvalidInputException("Stok tidak boleh negatif");
        }
    }
}