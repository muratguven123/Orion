package org.murat.orion.Payment.İnterfaces;

import org.murat.orion.AccountDomain.Entity.Account;

public interface AccountİntegrationService {
    void debit(Long accountid, double amount, String email, String phoneNumber);
    void credit(Long accountid, double amount, String email, String phoneNumber);
    boolean validateBalance(Long accountid, double amount);
}
