package com.upb.agripos.exception;

/**
 * Exception yang digunakan ketika input tidak valid pada service layer.
 */
public class InvalidInputException extends Exception {

    public InvalidInputException() {
        super();
    }

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
    }
}