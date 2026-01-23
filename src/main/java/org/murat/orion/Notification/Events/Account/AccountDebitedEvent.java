package org.murat.orion.Notification.Events.Account;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AccountDebitedEvent {
    private Long accountId;
    private BigDecimal amount;
    private BigDecimal previousBalance;
    private BigDecimal newBalance;
    private String currency;
    private String transactionReference;
    private LocalDateTime debitedAt;

    public AccountDebitedEvent(Long accountId, BigDecimal amount, String currency, LocalDateTime debitedAt, BigDecimal newBalance, BigDecimal previousBalance, String transactionReference) {
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.debitedAt = debitedAt;
        this.newBalance = newBalance;
        this.previousBalance = previousBalance;
        this.transactionReference = transactionReference;
    }
}
