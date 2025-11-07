package com.mendel.mendel_challenge.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
    
    public ResourceAlreadyExistsException(String resource, Long id) {
        super(String.format("%s already exists with id: %d", resource, id));
    }
}
