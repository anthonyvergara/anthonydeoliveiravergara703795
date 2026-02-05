package com.anthony.backend.domain.exception;

public class GlobalExceptionHandler extends RuntimeException {

    public GlobalExceptionHandler(String message) {
        super(message);
    }

    public GlobalExceptionHandler(String message, Throwable cause) {
        super(message, cause);
    }
}

