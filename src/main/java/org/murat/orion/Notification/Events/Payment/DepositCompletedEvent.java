package org.murat.orion.Notification.Events.Payment;

import lombok.Data;
import org.murat.orion.Payment.Entity.PaymentTransactionType;

@Data
public class DepositCompletedEvent {
    long accountId;
    double amount;
    String currency;
    String email;
    String phoneNumber;
    String subject;
    PaymentTransactionType paymentType;

    public DepositCompletedEvent(PaymentTransactionType paymentType, String currency, double amount, long accountId, String email, String phoneNumber, String subject) {
        this.paymentType = paymentType;
        this.currency = currency;
        this.amount = amount;
        this.accountId = accountId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subject = subject;
    }
}
