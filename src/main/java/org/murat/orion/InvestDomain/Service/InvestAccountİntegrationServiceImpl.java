package org.murat.orion.InvestDomain.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.AccountDomain.Entity.Account;
import org.murat.orion.AccountDomain.Repository.AccountRepository;
import org.murat.orion.InvestDomain.İnterface.InvestAccountİntegrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestAccountİntegrationServiceImpl implements InvestAccountİntegrationService {

    private final AccountRepository accountRepository;

    private Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı: " + accountId));
    }

    @Override
    @Transactional
    public void debitBalance(Long accountId, BigDecimal amount) {
        log.info("INVEST: Hesap {} üzerinden {} tutarında çekim yapılıyor...", accountId, amount);

        Account account = getAccountById(accountId);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz Bakiye! Yatırım yapılamaz.");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        log.info("INVEST: Hesap {} üzerinden {} tutarında çekim yapıldı.", accountId, amount);
    }

    @Override
    @Transactional
    public void creditBalance(Long accountId, BigDecimal amount) {
        log.info("INVEST: Hesap {} üzerine {} tutarında yatırma yapılıyor...", accountId, amount);

        Account account = getAccountById(accountId);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        log.info("INVEST: Hesap {} üzerine {} tutarında yatırma yapıldı.", accountId, amount);
    }

    @Override
    public BigDecimal getCurrentBalance(Long accountId) {
        Account account = getAccountById(accountId);
        return account.getBalance();
    }
}
