package org.murat.orion.invest_service.interfaces;

import org.murat.orion.invest_service.dto.request.BalanceRequest;

import java.math.BigDecimal;

public interface InvestAccountIntegrationService {
    void debitBalance(BalanceRequest request);

    void creditBalance(BalanceRequest request);

    BigDecimal getCurrentBalance(Long userId);
}
