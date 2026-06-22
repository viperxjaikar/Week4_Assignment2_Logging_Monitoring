package com.logging.exception;

/**
 * @author Gonuguntala Jaikar Ramu
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
