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
        return InvestType.Stock;
    }

    @Override
    public void validExecute(String symbol) {
        LocalTime now = LocalTime.now();
        LocalTime marketOpen = LocalTime.of(9, 30);
        LocalTime marketClose = LocalTime.of(16, 0);
        if (now.isBefore(marketOpen) && now.isBefore(marketClose)) {
            log.warn("StockInvestmentStrategy: Piyasa saatleri dışında işlem yapılamaz.");
            throw new RuntimeException("Piyasa saatleri dışında işlem yapılamaz.");
        }
        log.info("StockInvestmentStrategy: Piyasa saatleri içinde işlem yapılabilir.");

    }

}