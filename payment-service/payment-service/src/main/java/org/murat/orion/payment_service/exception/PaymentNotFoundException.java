package org.murat.orion.payment_service.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(UUID id) {
        super("Payment not found with id: " + id);
    }
}
