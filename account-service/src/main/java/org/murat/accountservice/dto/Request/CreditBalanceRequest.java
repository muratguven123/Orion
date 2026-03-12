package org.murat.accountservice.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CreditBalanceRequest {
    private long userId;
    private BigDecimal amount;
}
