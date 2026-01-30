package com.upb.agripos.exception;

public class DatabaseException extends Exception {
    
    // Constructor dengan pesan
    public DatabaseException(String message) {
        super(message);
    }
    
    // Constructor dengan pesan dan cause (exception asli)
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}