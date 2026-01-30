package com.upb.agripos.dao;

import com.upb.agripos.model.Product;
import java.util.List;

/**
 * Interface DAO untuk Product
 * Menerapkan DIP - Dependency Inversion Principle (Bab 6)
 */
public interface ProductDAO {
    void insert(Product product) throws Exception;
    void update(Product product) throws Exception;
    void delete(String code) throws Exception;
    Product findByCode(String code) throws Exception;
    List<Product> findAll() throws Exception;
}