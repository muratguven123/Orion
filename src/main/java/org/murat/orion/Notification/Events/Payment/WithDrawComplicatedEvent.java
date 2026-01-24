package org.murat.orion.Notification.Events.Payment;

import lombok.Data;

@Data
public class WithDrawComplicatedEvent {
    long accountId;
    double amount;
    String currency;
    String email;
    String phoneNumber;
    String subject;

    public WithDrawComplicatedEvent(String currency, double amount, long accountId, String email, String phoneNumber, String subject) {
        this.currency = currency;
        this.amount = amount;
        this.accountId = accountId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subject = subject;
    }
}
