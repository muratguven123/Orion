package org.murat.orion.AccountDomain.Exception;

public class AccountAlreadyExistsException extends AccountException {

    public AccountAlreadyExistsException(String message) {
        super(message);
    }

    public AccountAlreadyExistsException(String accountName, Long userId) {
        super("Account with name '" + accountName + "' already exists for user: " + userId);
    }
}
