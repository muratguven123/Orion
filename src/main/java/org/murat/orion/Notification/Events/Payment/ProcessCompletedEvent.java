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
    private PaymentTransactionType paymentType;

    public ProcessCompletedEvent(PaymentTransactionType paymentType, String currency, Double amount, Long sourceAccountId, Long targetAccountId, String description) {
        this.paymentType = paymentType;
        this.currency = currency;
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.description = description;
    }
}
