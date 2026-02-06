package org.murat.orion.AccountDomain.Exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends AccountException {

    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(BigDecimal currentBalance, BigDecimal requiredAmount) {
        super("Insufficient balance. Current: " + currentBalance + ", Required: " + requiredAmount);
    }
}
