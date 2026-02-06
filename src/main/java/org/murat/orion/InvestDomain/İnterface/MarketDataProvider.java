package org.murat.orion.InvestDomain.İnterface;

import java.math.BigDecimal;

public interface MarketDataProvider {
    BigDecimal getCurrentPrice(String symbol);

    boolean isValidSymbol(String symbol);
}
