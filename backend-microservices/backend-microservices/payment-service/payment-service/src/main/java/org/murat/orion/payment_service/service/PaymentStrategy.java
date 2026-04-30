package org.murat.orion.payment_service.service;

import org.murat.orion.payment_service.dto.request.PaymentRequest;
import org.murat.orion.payment_service.entity.PaymentTransactionType;

public interface PaymentStrategy {
    PaymentTransactionType getPaymentType();
    void processPayment(PaymentRequest request);
}
