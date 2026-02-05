package com.anthony.backend.domain.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s com ID %d não encontrado", resource, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

