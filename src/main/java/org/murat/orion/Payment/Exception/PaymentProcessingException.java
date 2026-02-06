package org.murat.orion.Payment.Exception;

public class PaymentProcessingException extends PaymentException {

    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
