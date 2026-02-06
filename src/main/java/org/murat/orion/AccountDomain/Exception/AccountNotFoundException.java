package org.murat.orion.AccountDomain.Exception;

public class AccountNotFoundException extends AccountException {

    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(Long accountId) {
        super("Account not found with id: " + accountId);
    }
}
