package org.murat.orion.payment_service.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.murat.orion.payment_service.entity.PaymentTransactionStatus;
import org.murat.orion.payment_service.entity.PaymentTransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentTransferRequest {
    private String referenceCode;
    private String sourceAccount;
    private String targetAccount;
    private Double amount;
    private String currency;
    private PaymentTransactionType type;
    private PaymentTransactionStatus status;
    private LocalDateTime date;
    private String description;
}
