package org.murat.orion.InvestDomain.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.murat.orion.InvestDomain.Entity.InvestType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestSellResponse {
    private Long investmentId;
    private Long userId;
    private String symbol;
    private InvestType type;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalRevenue;
    private BigDecimal profitLoss;
    private String message;
    private LocalDateTime transactionDate;
}
