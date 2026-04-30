package org.murat.orion.invest_service.interfaces;

import org.murat.orion.invest_service.entity.InvestType;

public interface InvestmentStrategy {
    InvestType getInvestType();
    void validExecute(String symbol);
}
