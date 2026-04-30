package org.murat.orion.payment_service.İnterface;

import org.murat.orion.payment_service.dto.request.BalanceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "account-service", path = "/api/accounts")
public interface AccountServiceClient {

    @PostMapping("/internal/debit")
    void debitBalance(@RequestBody BalanceRequest request);

    @PostMapping("/internal/credit")
    void creditBalance(@RequestBody BalanceRequest request);
}
