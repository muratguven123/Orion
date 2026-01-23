package org.murat.orion.Notification.Events.Account;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AccountCreditedEvent {
    private Long accountId;
    private BigDecimal amount;
    private BigDecimal previousBalance;
    private BigDecimal newBalance;
    private String currency;
    private String transactionReference;
    private LocalDateTime creditedAt;

    public AccountCreditedEvent(Long accountId, BigDecimal amount, LocalDateTime creditedAt, String currency, BigDecimal newBalance, BigDecimal previousBalance, String transactionReference) {
        this.accountId = accountId;
        this.amount = amount;
        this.creditedAt = creditedAt;
        this.currency = currency;
        this.newBalance = newBalance;
        this.previousBalance = previousBalance;
        this.transactionReference = transactionReference;
    }
}
