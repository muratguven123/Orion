package org.murat.orion.payment_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDepositEvent implements Serializable {
    private Long accountId;
    private BigDecimal amount;
    private String currency;
    private String referenceCode;
    private LocalDateTime timestamp;
}

