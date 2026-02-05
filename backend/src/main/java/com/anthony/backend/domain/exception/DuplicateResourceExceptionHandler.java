package com.anthony.backend.domain.exception;

public class DuplicateResourceExceptionHandler extends GlobalExceptionHandler {

    public DuplicateResourceExceptionHandler(String message) {
        super(message);
    }

    public DuplicateResourceExceptionHandler(String resource, String field, String value) {
        super(String.format("%s com %s '%s' já existe", resource, field, value));
    }
}

