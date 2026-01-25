package org.murat.orion.AuthDomain.Exception;

import jakarta.security.auth.message.AuthException;

public class UserNotFoundException extends AuthException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String username) {
        super("User not found with username: " + username);
    }
}

