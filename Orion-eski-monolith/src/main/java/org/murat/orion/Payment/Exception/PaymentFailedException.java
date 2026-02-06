package org.murat.orion.Payment.Exception;

public class PaymentFailedException extends PaymentException {

    public PaymentFailedException(String message) {
        super(message);
    }

    public PaymentFailedException(String paymentId, String reason) {
        super("Payment failed for id: " + paymentId + ". Reason: " + reason);
    }
}
