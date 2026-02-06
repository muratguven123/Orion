package org.murat.orion.invest_service.service;

import org.murat.orion.invest_service.interfaces.InvestAccountIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock implementation of InvestAccountIntegrationService
 * In production, this would integrate with account-service via Feign client
 */
@Service
public class InvestAccountIntegrationServiceImpl implements InvestAccountIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(InvestAccountIntegrationServiceImpl.class);

    // Mock balance storage - in production, this would call account-service
    private final Map<Long, BigDecimal> balanceMap = new ConcurrentHashMap<>();

    @Override
    public void debitBalance(Long userId, BigDecimal amount) {
        log.info("Bakiye cekiliyor: userId={}, amount={}", userId, amount);
        BigDecimal currentBalance = balanceMap.getOrDefault(userId, BigDecimal.valueOf(100000));

        if (currentBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz bakiye. Mevcut: " + currentBalance + ", Istenen: " + amount);
        }

        balanceMap.put(userId, currentBalance.subtract(amount));
        log.info("Bakiye guncellendi: userId={}, yeniBakiye={}", userId, balanceMap.get(userId));
    }

    @Override
    public void creditBalance(Long userId, BigDecimal amount) {
        log.info("Bakiye ekleniyor: userId={}, amount={}", userId, amount);
        BigDecimal currentBalance = balanceMap.getOrDefault(userId, BigDecimal.valueOf(100000));
        balanceMap.put(userId, currentBalance.add(amount));
        log.info("Bakiye guncellendi: userId={}, yeniBakiye={}", userId, balanceMap.get(userId));
    }

    @Override
    public BigDecimal getCurrentBalance(Long userId) {
        return balanceMap.getOrDefault(userId, BigDecimal.valueOf(100000));
    }
}
