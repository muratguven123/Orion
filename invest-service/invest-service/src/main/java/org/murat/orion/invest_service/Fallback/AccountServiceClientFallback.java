package org.murat.orion.invest_service.Fallback;

import org.murat.orion.invest_service.dto.request.BalanceRequest;
import org.murat.orion.invest_service.exception.ExternalServiceUnavailableException;
import org.murat.orion.invest_service.interfaces.AccountServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AccountServiceClientFallback implements AccountServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceClientFallback.class);

    @Override
    public void debitBalance(BalanceRequest request) {
        log.error("Devre Kesici (Circuit Breaker) AÇIK! Account-Service'e ulaşılamıyor. Düşülen Fallback: debitBalance. İşlem iptal edildi. Kullanıcı: {}, Tutar: {}", request.getUserId(), request.getAmount());
        throw new ExternalServiceUnavailableException("Account Service");
    }

    @Override
    public void creditBalance(BalanceRequest request) {
        log.error("Devre Kesici (Circuit Breaker) AÇIK! Account-Service'e ulaşılamıyor. Düşülen Fallback: creditBalance. İşlem iptal edildi. Kullanıcı: {}, Tutar: {}", request.getUserId(), request.getAmount());
        throw new ExternalServiceUnavailableException("Account Service");
    }
}
