package com.mendel.mendel_challenge.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s not found with id: %d", resource, id));
    }
    
    public ResourceNotFoundException(String resource, String value) {
        super(String.format("%s not found with value: %s", resource, value));
    }
}
