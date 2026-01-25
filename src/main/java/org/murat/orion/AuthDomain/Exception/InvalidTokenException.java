package org.murat.orion.AuthDomain.Exception;

import jakarta.security.auth.message.AuthException;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException() {
        super("Invalid or expired token");
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
