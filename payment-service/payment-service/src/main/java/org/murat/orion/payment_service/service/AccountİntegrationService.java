package org.murat.orion.payment_service.service;

public interface AccountİntegrationService {
    void debit(Long accountid, double amount, String email, String phoneNumber);
    void credit(Long accountid, double amount, String email, String phoneNumber);
    boolean validateBalance(Long accountid, double amount);
}
