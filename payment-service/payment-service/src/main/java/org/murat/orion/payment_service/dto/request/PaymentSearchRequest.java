package org.murat.orion.payment_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.murat.orion.payment_service.entity.PaymentTransactionType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSearchRequest {
    private PaymentTransactionType type;
    private String currency;
    private Double minAmount;
    private Double maxAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}
