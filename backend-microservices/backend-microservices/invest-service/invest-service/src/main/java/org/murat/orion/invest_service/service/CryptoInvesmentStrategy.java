package org.murat.orion.invest_service.service;

import org.murat.orion.invest_service.entity.InvestType;
import org.murat.orion.invest_service.interfaces.InvestmentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CryptoInvesmentStrategy implements InvestmentStrategy {

    private static final Logger log = LoggerFactory.getLogger(CryptoInvesmentStrategy.class);
    @Override
    public InvestType getInvestType() {
        return InvestType.CRYPTO;
    }

    @Override
    public void validExecute(String symbol) {
        log.info("CryptoInvestmentStrategy: Kripto para birimleri 7/24 işlem görebilir.");
    }
}
