package org.murat.orion.invest_service.mapper;

import org.murat.orion.invest_service.dto.request.InvesmentRequest;
import org.murat.orion.invest_service.entity.Investment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InvestMapper {

    public Investment toEntity(InvesmentRequest request, BigDecimal currentPrice, BigDecimal totalCost) {
        return Investment.builder()
                .userId(request.getUserId())
                .symbol(request.getSymbol())
                .investType(request.getType())
                .quantity(request.getQuantity())
                .price(currentPrice)
                .amount(totalCost)
                .build();
    }
}
