package com.anthony.backend.domain.exception;

public class ResourceNotFoundExceptionHandler extends GlobalExceptionHandler {

    public ResourceNotFoundExceptionHandler(String resource, Long id) {
        super(String.format("%s com ID %d não encontrado", resource, id));
    }

    public ResourceNotFoundExceptionHandler(String message) {
        super(message);
    }
}

