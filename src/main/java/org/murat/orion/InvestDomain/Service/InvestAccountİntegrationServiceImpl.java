package org.murat.orion.InvestDomain.Service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.AccountDomain.Entity.Account;
import org.murat.orion.AccountDomain.Repository.AccountRepository;
import org.murat.orion.InvestDomain.İnterface.InvestAccountİntegrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestAccountİntegrationServiceImpl implements InvestAccountİntegrationService {

    private final AccountRepository accountRepository;

    private Account getActiveAccountByUserId(Long userId) {
        List<Account> accounts = accountRepository.findByUserIdAndIsActiveTrue(userId);
        if (accounts == null || accounts.isEmpty()) {
            accounts = accountRepository.findByUserId(userId);
            if (accounts == null || accounts.isEmpty()) {
                throw new RuntimeException("Kullanıcıya ait hesap bulunamadı: " + userId);
            }
        }
        return accounts.get(0);
    }

    @Override
    @Transactional
    public void debitBalance(Long userId, BigDecimal amount) {
        log.info("INVEST: Kullanıcı {} için {} tutarında çekim yapılıyor...", userId, amount);

        Account account = getActiveAccountByUserId(userId);
        log.info("INVEST: Kullanıcının aktif hesabı bulundu. Hesap ID: {}", account.getId());

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz Bakiye! Yatırım yapılamaz. Mevcut bakiye: " + account.getBalance());
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        log.info("INVEST: Hesap {} üzerinden {} tutarında çekim yapıldı. Yeni bakiye: {}",
                account.getId(), amount, account.getBalance());
    }

    @Override
    @Transactional
    public void creditBalance(Long userId, BigDecimal amount) {
        log.info("INVEST: Kullanıcı {} için {} tutarında yatırma yapılıyor...", userId, amount);

        Account account = getActiveAccountByUserId(userId);
        log.info("INVEST: Kullanıcının aktif hesabı bulundu. Hesap ID: {}", account.getId());

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        log.info("INVEST: Hesap {} üzerine {} tutarında yatırma yapıldı. Yeni bakiye: {}",
                account.getId(), amount, account.getBalance());
    }

    @Override
    public BigDecimal getCurrentBalance(Long userId) {
        Account account = getActiveAccountByUserId(userId);
        return account.getBalance();
    }
}
