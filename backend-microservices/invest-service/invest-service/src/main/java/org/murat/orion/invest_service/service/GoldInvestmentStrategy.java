package org.murat.orion.invest_service.service;

import org.murat.orion.invest_service.entity.InvestType;
import org.murat.orion.invest_service.interfaces.InvestmentStrategy;
import org.springframework.stereotype.Component;

@Component
public class GoldInvestmentStrategy  implements InvestmentStrategy {
    @Override
    public InvestType getInvestType() {
        return InvestType.GOLD;
    }

    @Override
    public void validExecute(String symbol) {
        System.out.println("Altın İşlemleriniz yapılıyor .");
    }
}
