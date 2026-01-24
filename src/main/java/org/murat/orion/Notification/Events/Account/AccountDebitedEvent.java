package org.murat.orion.Notification.Events.Account;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AccountDebitedEvent {
    private Long accountId;
    private String email;
    private String phoneNumber;
    private String subject;
    private BigDecimal amount;
    private BigDecimal previousBalance;
    private BigDecimal newBalance;
    private String currency;
    private String transactionReference;
    private LocalDateTime debitedAt;

    public AccountDebitedEvent(Long accountId, BigDecimal amount, String currency, LocalDateTime debitedAt, BigDecimal newBalance, BigDecimal previousBalance, String transactionReference, String email, String phoneNumber, String subject) {
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.debitedAt = debitedAt;
        this.newBalance = newBalance;
        this.previousBalance = previousBalance;
        this.transactionReference = transactionReference;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subject = subject;
    }
}
