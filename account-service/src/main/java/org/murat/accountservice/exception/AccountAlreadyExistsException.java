package org.murat.accountservice.exception;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(String message) {
        super(message);
    }

    public AccountAlreadyExistsException(String accountName, Long userId) {
        super("Account with name '" + accountName + "' already exists for user: " + userId);
    }
}
