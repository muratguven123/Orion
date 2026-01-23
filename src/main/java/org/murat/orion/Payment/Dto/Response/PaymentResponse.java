package org.murat.orion.Payment.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.murat.orion.Payment.Entity.PaymentTransactionStatus;
import org.murat.orion.Payment.Entity.PaymentTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private String currency;
    private PaymentTransactionType type;
    private PaymentTransactionStatus status;
    private String referenceCode;
    private String description;
    private String failureReason;
    private LocalDateTime createdAt;
}
