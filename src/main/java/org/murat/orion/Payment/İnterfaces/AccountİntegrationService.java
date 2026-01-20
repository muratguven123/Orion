package org.murat.orion.Payment.İnterfaces;

import org.murat.orion.AccountDomain.Entity.Account;

public interface AccountİntegrationService {
    void debit(Long accountid , double amount);
    void credit(Long accountid , double amount);
    boolean validateBalance(Long accountid , double amount);
}
