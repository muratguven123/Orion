package org.murat.orion.Payment.Dto.Request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.murat.orion.Payment.Entity.PaymentTransactionType;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PaymentRequest {
    private Long sourceAccountId;
    private Long targetAccountId;
    private Double amount;
    private String currency;
    private String description;
    private PaymentTransactionType paymentType;
}
