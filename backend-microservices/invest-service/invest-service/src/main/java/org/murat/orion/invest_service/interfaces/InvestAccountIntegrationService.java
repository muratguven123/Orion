package org.murat.orion.invest_service.interfaces;

import java.math.BigDecimal;

public interface InvestAccountIntegrationService {
    void debitBalance(Long userId, BigDecimal amount);

    void creditBalance(Long userId, BigDecimal amount);

    BigDecimal getCurrentBalance(Long userId);
}
