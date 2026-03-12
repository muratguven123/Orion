package org.murat.accountservice.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
    public InsufficientBalanceException(BigDecimal currentBalance, BigDecimal requiredAmount) {
        super("Insufficient balance. Current: " + currentBalance + ", Required: " + requiredAmount);
    }
}
