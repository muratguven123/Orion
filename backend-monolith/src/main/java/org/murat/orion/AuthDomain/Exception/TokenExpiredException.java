package org.murat.orion.AuthDomain.Exception;

import jakarta.security.auth.message.AuthException;

public class TokenExpiredException extends AuthException {

    public TokenExpiredException() {
        super("Token has expired");
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
