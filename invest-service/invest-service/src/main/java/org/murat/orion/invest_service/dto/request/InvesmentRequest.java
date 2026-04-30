package org.murat.orion.invest_service.dto.request;

import org.murat.orion.invest_service.entity.InvestType;

import java.math.BigDecimal;

public class InvesmentRequest {
    private Long userId;
    private String symbol;
    private BigDecimal quantity;
    private InvestType type;

    public InvesmentRequest() {}

    public InvesmentRequest(Long userId, String symbol, BigDecimal quantity, InvestType type) {
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.type = type;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public InvestType getType() { return type; }
    public void setType(InvestType type) { this.type = type; }
}