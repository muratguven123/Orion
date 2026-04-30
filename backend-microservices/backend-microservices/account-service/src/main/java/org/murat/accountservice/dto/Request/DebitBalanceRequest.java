package org.murat.accountservice.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DebitBalanceRequest {
    private long userId;
    private BigDecimal amount;
}
