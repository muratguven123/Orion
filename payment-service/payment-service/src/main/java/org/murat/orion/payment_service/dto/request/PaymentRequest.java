package org.murat.orion.payment_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.murat.orion.payment_service.entity.PaymentTransactionType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    private Long sourceAccountId;

    private Long targetAccountId;

    private BigDecimal amount;

    private String currency;

    private PaymentTransactionType type;

    private String description;
}
