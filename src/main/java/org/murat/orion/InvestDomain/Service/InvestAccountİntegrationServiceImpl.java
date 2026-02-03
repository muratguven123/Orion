package org.murat.orion.InvestDomain.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.AccountDomain.Entity.Account;
import org.murat.orion.AccountDomain.Repository.AccountRepository;
import org.murat.orion.AccountDomain.Service.AccountService;
import org.murat.orion.InvestDomain.İnterface.InvestAccountİntegrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
@Service
@RequiredArgsConstructor
@Slf4j
public class InvestAccountİntegrationServiceImpl implements InvestAccountİntegrationService {

    private final AccountRepository accountRepository;
    private final AccountService accountService;


    @Override
    @Transactional
    public void debitBalance(Long userId, BigDecimal amount) {
    log.info("INVEST: Kullanıcı {} hesabından {} tutarında çekim yapılıyor...," + userId, amount);

        Account account = (Account) accountRepository.findByUserId(userId);
        accountService.debitv2(account.getUserId(), amount);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz Bakiye! Yatırım yapılamaz.");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        log.info("INVEST: Kullanıcı {} hesabından {} tutarında çekim yapıldı.", userId, amount);
    }

    @Override
    public void creditBalance(Long userId, BigDecimal amount) {
        log.info("INVEST: Kullanıcı {} hesabına {} tutarında yatırma yapılıyor...", userId, amount);
        Account account = (Account) accountRepository.findByUserId(userId);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

    }

    @Override
    public BigDecimal getCurrentBalance(Long userId) {
        Account account = (Account) accountRepository.findByUserId(userId);
        return account.getBalance();
    }
}
