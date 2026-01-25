package org.murat.orion.AuthDomain.Exception;

public class InvalidCredentialsException extends AuthException {

    public InvalidCredentialsException() {
        super("Invalid credentials provided");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
