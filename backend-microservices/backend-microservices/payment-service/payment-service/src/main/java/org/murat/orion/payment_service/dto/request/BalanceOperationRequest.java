package org.murat.orion.payment_service.dto.request;

import lombok.Data;

@Data
public class BalanceOperationRequest {
    private Long accountId;
    private Double amount;
    private String currency;
    private String email;
    private String phoneNumber;
}

