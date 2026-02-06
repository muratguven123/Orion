package org.murat.orion.InvestDomain.İnterface;

import java.math.BigDecimal;

public interface InvestAccountİntegrationService {
    void debitBalance(Long userId, BigDecimal amount);

    void creditBalance(Long userId, BigDecimal amount);

    BigDecimal getCurrentBalance(Long userId);
}
