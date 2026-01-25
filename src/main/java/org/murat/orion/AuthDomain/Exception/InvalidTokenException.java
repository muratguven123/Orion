package org.murat.orion.AuthDomain.Exception;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException() {
        super("Invalid or expired token");
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
