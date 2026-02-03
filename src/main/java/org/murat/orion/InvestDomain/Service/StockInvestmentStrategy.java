package org.murat.orion.InvestDomain.Service;

import lombok.extern.slf4j.Slf4j;
import org.murat.orion.InvestDomain.Entity.InvestType;
import org.murat.orion.InvestDomain.İnterface.InvesmentStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@Slf4j
public class StockInvestmentStrategy implements InvesmentStrategy {
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