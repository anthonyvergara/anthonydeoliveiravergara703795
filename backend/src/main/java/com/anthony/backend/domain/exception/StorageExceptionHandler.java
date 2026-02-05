package com.anthony.backend.domain.exception;

public class StorageExceptionHandler extends GlobalExceptionHandler {

    public StorageExceptionHandler(String message) {
        super(message);
    }

    public StorageExceptionHandler(String message, Throwable cause) {
        super(message, cause);
    }
}

