package org.murat.orion.invest_service.interfaces;

import java.math.BigDecimal;

public interface MarketDataProvider {
    BigDecimal getCurrentPrice(String symbol);

    boolean isValidSymbol(String symbol);
}
