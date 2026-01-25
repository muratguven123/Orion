package org.murat.orion.AccountDomain.Exception;

public class AccountNotActiveException extends AccountException {

    public AccountNotActiveException(String message) {
        super(message);
    }

    public AccountNotActiveException(Long accountId) {
        super("Account is not active with id: " + accountId);
    }
}
