package org.murat.orion.Notification.Events.Payment;

import lombok.Data;

@Data
public class WithDrawComplicatedEvent {
    long accountId;
    double amount;
    String currency;
    public WithDrawComplicatedEvent( String currency, double amount, long accountId) {
        this.currency = currency;
        this.amount = amount;
        this.accountId = accountId;
    }
}
