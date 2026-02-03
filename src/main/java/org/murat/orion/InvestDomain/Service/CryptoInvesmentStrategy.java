package org.murat.orion.InvestDomain.Service;

import lombok.extern.slf4j.Slf4j;
import org.murat.orion.InvestDomain.Entity.InvestType;
import org.murat.orion.InvestDomain.İnterface.InvesmentStrategy;
import org.springframework.stereotype.Component;
;

@Component
@Slf4j
public class CryptoInvesmentStrategy implements InvesmentStrategy {
    @Override
    public InvestType getInvestType() {
        return InvestType.Crypto;
    }

    @Override
    public void validExecute(String symbol) {
        log.info("CryptoInvestmentStrategy: Kripto para birimleri 7/24 işlem görebilir.");
    }
}
