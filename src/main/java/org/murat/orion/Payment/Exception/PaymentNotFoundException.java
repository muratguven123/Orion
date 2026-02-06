package org.murat.orion.Payment.Exception;

public class PaymentNotFoundException extends PaymentException {

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
