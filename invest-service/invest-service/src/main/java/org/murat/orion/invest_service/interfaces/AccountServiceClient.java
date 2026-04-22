package org.murat.orion.invest_service.interfaces;

import io.github.resilience4j.retry.annotation.Retry;
import org.murat.orion.invest_service.Fallback.AccountServiceClientFallback;
import org.murat.orion.invest_service.dto.request.BalanceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service", path = "/api/accounts", fallback = AccountServiceClientFallback.class)
@Retry(name = "account-service")
public interface AccountServiceClient {
    @PostMapping("/internal/debit")
    void debitBalance(@RequestBody BalanceRequest request);

    @PostMapping("/internal/credit")
    void creditBalance(@RequestBody BalanceRequest request);
}
