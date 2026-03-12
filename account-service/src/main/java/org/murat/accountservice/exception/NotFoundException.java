package org.murat.accountservice.exception;

/**
 * Simple custom runtime exception for the exception package.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException() { super(); }
    public NotFoundException(String message) { super(message); }
}
