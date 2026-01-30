package com.upb.agripos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.upb.agripos.model.Product;

public class ProductTest {
    
    private Product product;
    
    @BeforeEach
    public void setUp() {
        product = new Product("P01", "Benih Jagung", 25000, 50);
    }
    
    @Test
    public void testProductName() {
        assertEquals("Benih Jagung", product.getName());
    }
    
    @Test
    public void testProductCode() {
        assertEquals("P01", product.getCode());
    }
    
    @Test
    public void testProductPrice() {
        assertEquals(25000, product.getPrice());
    }
    
    @Test
    public void testProductStock() {
        assertEquals(50, product.getStock());
    }
    
    @Test
    public void testUpdatePrice() {
        product.setPrice(30000);
        assertEquals(30000, product.getPrice());
    }
    
    @Test
    public void testUpdateStock() {
        product.setStock(75);
        assertEquals(75, product.getStock());
    }
    
    @Test
    public void testProductNotNull() {
        assertNotNull(product);
        assertNotNull(product.getName());
        assertNotNull(product.getCode());
    }
}