package org.murat.orion.AuthDomain.Exception;

public class UserAlreadyExistsException extends AuthException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String email, String phoneNumber) {
        super("User already exists with email: " + email + " or phone: " + phoneNumber);
    }
}
