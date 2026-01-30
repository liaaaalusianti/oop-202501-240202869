package com.upb.agripos;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.upb.agripos.config.DatabaseConnection;

public class DatabaseConnectionTest {
    
    @Test
    public void testSingletonInstance() {
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        
        // Harus mengembalikan instance yang sama
        assertSame(db1, db2);
    }
    
    @Test
    public void testConnectionNotNull() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        assertNotNull(db);
    }
    
    @Test
    public void testConnectionString() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        assertNotNull(db.getConnectionString());
        assertTrue(db.getConnectionString().contains("agripos"));
    }
    
    @Test
    public void testConnect() {
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.connect();
        assertTrue(db.isConnected());
    }
}
