package org.murat.orion.payment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.payment_service.dto.request.BalanceRequest;
import org.murat.orion.payment_service.İnterface.AccountServiceClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountİntegrationServiceImpl implements AccountİntegrationService {

    private final AccountServiceClient accountServiceClient;

    @Override
    public void debit(BalanceRequest request) {
        log.info("Bakiye çekiliyor: userId={}, amount={}", request.getUserId(), request.getAmount());
        accountServiceClient.debitBalance(request);
    }

    @Override
    public void credit(BalanceRequest request) {
        log.info("Bakiye ekleniyor: userId={}, amount={}", request.getUserId(), request.getAmount());
        accountServiceClient.creditBalance(request);
    }
}
