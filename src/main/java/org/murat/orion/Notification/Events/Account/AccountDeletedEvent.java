package org.murat.orion.Notification.Events.Account;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AccountDeletedEvent {
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private BigDecimal finalBalance;
    private LocalDateTime deletedAt;

    public AccountDeletedEvent(Long accountId, String accountNumber, LocalDateTime deletedAt, BigDecimal finalBalance, Long userId) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.deletedAt = deletedAt;
        this.finalBalance = finalBalance;
        this.userId = userId;
    }
}
