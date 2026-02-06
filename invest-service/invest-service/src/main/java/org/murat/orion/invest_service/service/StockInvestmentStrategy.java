package org.murat.orion.invest_service.service;

import org.murat.orion.invest_service.entity.InvestType;
import org.murat.orion.invest_service.interfaces.InvestmentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class StockInvestmentStrategy implements InvestmentStrategy {

    private static final Logger log = LoggerFactory.getLogger(StockInvestmentStrategy.class);
    @Override
    public InvestType getInvestType() {
        return InvestType.STOCK;
    }

    @Override
    public void validExecute(String symbol) {
        LocalTime now = LocalTime.now();
        LocalTime marketOpen = LocalTime.of(9, 30);
        LocalTime marketClose = LocalTime.of(18, 0);

        if (now.isBefore(marketOpen) || now.isAfter(marketClose)) {
            log.warn("StockInvestmentStrategy: Piyasa saatleri dışında (09:30-18:00), ancak işleme devam ediliyor.");
        }
        log.info("StockInvestmentStrategy: {} sembolü için işlem onaylandı.", symbol);
    }

}
