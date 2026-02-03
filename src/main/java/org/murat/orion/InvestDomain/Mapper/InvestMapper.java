package org.murat.orion.InvestDomain.Mapper;

import org.murat.orion.InvestDomain.Dto.Request.InvesmentRequest;
import org.murat.orion.InvestDomain.Entity.Invesment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InvestMapper {

    public Invesment toEntity(InvesmentRequest request, BigDecimal currentPrice, BigDecimal totalCost) {
        return Invesment.builder()
                .userId(request.getUserId())
                .symbol(request.getSymbol())
                .i̇nvestType(request.getType())
                .quantity(request.getQuantity())
                .price(currentPrice)
                .amount(totalCost)
                .build();
    }
}
