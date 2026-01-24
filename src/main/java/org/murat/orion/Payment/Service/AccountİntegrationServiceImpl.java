package org.murat.orion.Payment.Service;

import lombok.RequiredArgsConstructor;
import org.murat.orion.AccountDomain.Entity.Account;
import org.murat.orion.AccountDomain.Service.AccountService;
import org.murat.orion.Payment.İnterfaces.AccountİntegrationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountİntegrationServiceImpl implements AccountİntegrationService {
    private final AccountService accountService;

    @Override
    public void debit(Long accountid, double amount, String email, String phoneNumber) {
        accountService.debit(accountid, BigDecimal.valueOf(amount), email, phoneNumber);
    }

    @Override
    public void credit(Long accountid, double amount, String email, String phoneNumber) {
        accountService.credit(accountid, BigDecimal.valueOf(amount), email, phoneNumber);
    }

    @Override
    public boolean validateBalance(Long accountid, double amount) {
        return accountService.hasSufficientBalance(accountid, BigDecimal.valueOf(amount));
    }
}
