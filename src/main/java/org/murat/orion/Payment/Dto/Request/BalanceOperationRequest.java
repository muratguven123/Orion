package org.murat.orion.Payment.Dto.Request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BalanceOperationRequest {
    private Long accountId;
    private Double amount;
    private String currency;
    private String email;
    private String phoneNumber;
}
