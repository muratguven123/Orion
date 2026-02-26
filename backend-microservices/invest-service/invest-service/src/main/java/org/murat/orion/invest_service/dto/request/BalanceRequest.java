package org.murat.orion.invest_service.dto.request;

import java.math.BigDecimal;

public class BalanceRequest {
    private Long userId;
    private BigDecimal amount;

    public BalanceRequest() {}

    public BalanceRequest(Long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
