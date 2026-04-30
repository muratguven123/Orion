package org.murat.orion.invest_service.service;

import org.murat.orion.invest_service.interfaces.MarketDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MockMarketAdapter implements MarketDataProvider {

    private static final Logger log = LoggerFactory.getLogger(MockMarketAdapter.class);
    private final Map<String, BigDecimal> basePrices = new HashMap<>();
    private final Random random = new Random();


    public MockMarketAdapter() {
        basePrices.put("AAPL", BigDecimal.valueOf(150.00));
        basePrices.put("GOOGL", BigDecimal.valueOf(2800.00));
        basePrices.put("MSFT", BigDecimal.valueOf(300.00));
        basePrices.put("AMZN", BigDecimal.valueOf(3500.00));
        basePrices.put("GOOG", BigDecimal.valueOf(4000.00));
        basePrices.put("NVDIA", BigDecimal.valueOf(5500.00));
        basePrices.put("TSLA", BigDecimal.valueOf(700.00));
        basePrices.put("IBM", BigDecimal.valueOf(140.00));
    }


    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        BigDecimal baseprice= basePrices.getOrDefault(symbol.toUpperCase(), BigDecimal.valueOf(100.00));
        double fluctuation = (random.nextDouble() * 0.1) - 0.05;
        BigDecimal currentPrice = baseprice.add(baseprice.multiply(BigDecimal.valueOf(fluctuation)));
        currentPrice = currentPrice.setScale(2, java.math.RoundingMode.HALF_UP);
        log.info("MockMarketAdapter: Current price for {} is {}", symbol, currentPrice);
        return currentPrice;
    }

    @Override
    public boolean isValidSymbol(String symbol) {
        return basePrices.containsKey(symbol.toUpperCase());
    }
}
