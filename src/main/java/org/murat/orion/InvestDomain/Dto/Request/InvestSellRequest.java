package org.murat.orion.InvestDomain.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.murat.orion.InvestDomain.Entity.InvestType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestSellRequest {
    private Long userId;
    private Long accountId;
    private String symbol;
    private BigDecimal quantity;
    private InvestType type;
}
