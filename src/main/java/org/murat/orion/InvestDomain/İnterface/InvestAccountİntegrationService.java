package org.murat.orion.InvestDomain.İnterface;

import java.math.BigDecimal;

public interface InvestAccountİntegrationService {
    void debitBalance(Long accountId, BigDecimal amount);

    void creditBalance(Long accountId, BigDecimal amount);

    BigDecimal getCurrentBalance(Long accountId);
}
