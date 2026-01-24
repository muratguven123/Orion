package org.murat.orion.Notification.Events.Payment;

import lombok.Data;
import org.murat.orion.Payment.Entity.PaymentTransactionType;
@Data
public class ProcessCompletedEvent {
    private Long sourceAccountId;
    private Long targetAccountId;
    private Double amount;
    private String currency;
    private String description;
    private String email;
    private String phoneNumber;
    private String subject;
    private PaymentTransactionType paymentType;

    public ProcessCompletedEvent(PaymentTransactionType paymentType, String currency, Double amount, Long sourceAccountId, Long targetAccountId, String description, String email, String phoneNumber, String subject) {
        this.paymentType = paymentType;
        this.currency = currency;
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.description = description;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subject = subject;
    }
}
