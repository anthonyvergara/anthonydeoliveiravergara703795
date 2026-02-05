package com.anthony.backend.domain.exception;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resource, String field, String value) {
        super(String.format("%s com %s '%s' já existe", resource, field, value));
    }
}

