package org.murat.orion.invest_service.service;

import lombok.RequiredArgsConstructor;
import org.murat.orion.invest_service.interfaces.AccountServiceClient;
import org.murat.orion.invest_service.interfaces.InvestAccountIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class InvestAccountIntegrationServiceImpl implements InvestAccountIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(InvestAccountIntegrationServiceImpl.class);
    private final AccountServiceClient accountServiceClient;

    private final Map<Long, BigDecimal> balanceMap = new ConcurrentHashMap<>();

    @Override
    public void debitBalance(Long userId, BigDecimal amount) {
        log.info("Bakiye cekiliyor: userId={}, amount={}", userId, amount);
        accountServiceClient.debitBalance(new BalanceRequest(userId, amount));
    }

    @Override
    public void creditBalance(Long userId, BigDecimal amount) {
        log.info("Bakiye ekleniyor: userId={}, amount={}", userId, amount);
        accountServiceClient.creditBalance(new BalanceRequest(userId, amount));
    }

    @Override
    public BigDecimal getCurrentBalance(Long userId) {
        return balanceMap.getOrDefault(userId, BigDecimal.valueOf(100000));
    }
}
