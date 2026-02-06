package org.murat.orion.InvestDomain.Service;

import lombok.extern.slf4j.Slf4j;
import org.murat.orion.InvestDomain.İnterface.MarketDataProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class MockMarketAdapter implements MarketDataProvider {
    private final Map<String, BigDecimal> basePrices = new HashMap<>();
    private  final Random random = new Random();


    public MockMarketAdapter() {
        basePrices.put("AAPL", BigDecimal.valueOf(150.00));
        basePrices.put("GOOGL", BigDecimal.valueOf(2800.00));
        basePrices.put("MSFT", BigDecimal.valueOf(300.00));
        basePrices.put("AMZN", BigDecimal.valueOf(3500.00));
        basePrices.put("GOOG", BigDecimal.valueOf(4000.00));
        basePrices.put("MSFT", BigDecimal.valueOf(4500.00));
        basePrices.put("NVDİA", BigDecimal.valueOf(5500.00));
        basePrices.put("TSLA", BigDecimal.valueOf(700.00));
        basePrices.put("IBM", BigDecimal.valueOf(140.00));
    }


    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        BigDecimal baseprice= basePrices.getOrDefault(symbol.toUpperCase(), BigDecimal.valueOf(100.00));
        double fluctuation = (random.nextDouble() * 0.1) - 0.05;
        BigDecimal currentPrice = baseprice.add(baseprice.multiply(BigDecimal.valueOf(fluctuation)));
        currentPrice = currentPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
        log.info("MockMarketAdapter: Current price for {} is {}", symbol, currentPrice);
        return currentPrice;
    }

    @Override
    public boolean isValidSymbol(String symbol) {
        return basePrices.containsKey(symbol.toUpperCase());
    }
}
