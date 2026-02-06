package org.murat.orion.AuthDomain.Exception;

import jakarta.security.auth.message.AuthException;

public class UserNotFoundException extends AuthException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException withUsername(String username) {
        return new UserNotFoundException("User not found with username: " + username);
    }
}

