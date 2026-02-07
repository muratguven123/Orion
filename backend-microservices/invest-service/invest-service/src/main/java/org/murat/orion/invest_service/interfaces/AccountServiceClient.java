package org.murat.orion.invest_service.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "account-service", path = "/api/accounts")
public interface AccountServiceClient {
    @PostMapping("/internal/debit")
    void debitBalance(@RequestParam("userId") Long userId, @RequestParam("amount") BigDecimal amount);

    @PostMapping("/internal/credit")
    void creditBalance(@RequestParam("userId") Long userId, @RequestParam("amount") BigDecimal amount);
}
