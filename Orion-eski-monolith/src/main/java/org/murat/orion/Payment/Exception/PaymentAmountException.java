package org.murat.orion.Payment.Exception;

import java.math.BigDecimal;

public class PaymentAmountException extends PaymentException {

    public PaymentAmountException(String message) {
        super(message);
    }

    public PaymentAmountException(BigDecimal amount) {
        super("Invalid payment amount: " + amount);
    }
}
