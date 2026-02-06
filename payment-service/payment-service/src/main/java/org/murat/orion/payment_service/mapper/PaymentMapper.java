package org.murat.orion.payment_service.mapper;

import org.murat.orion.payment_service.dto.request.PaymentRequest;
import org.murat.orion.payment_service.dto.response.PaymentResponse;
import org.murat.orion.payment_service.entity.Payment;
import org.murat.orion.payment_service.entity.PaymentTransactionStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequest request) {
        return Payment.builder()
                .sourceAccountId(request.getSourceAccountId())
                .targetAccountId(request.getTargetAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type(request.getType())
                .status(PaymentTransactionStatus.PENDING)
                .referenceCode(generateReferenceCode())
                .description(request.getDescription())
                .build();
    }

    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
//                .id(payment.getId())
                .sourceAccountId(payment.getSourceAccountId())
                .targetAccountId(payment.getTargetAccountId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .type(payment.getType())
                .status(payment.getStatus())
                .referenceCode(payment.getReferenceCode())
                .description(payment.getDescription())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private String generateReferenceCode() {
        return "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
