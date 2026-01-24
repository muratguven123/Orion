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
    private String email;
    private String phoneNumber;
    private String subject;
    private BigDecimal finalBalance;
    private LocalDateTime deletedAt;

    public AccountDeletedEvent(Long accountId, String accountNumber, LocalDateTime deletedAt, BigDecimal finalBalance, Long userId, String email, String phoneNumber, String subject) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.deletedAt = deletedAt;
        this.finalBalance = finalBalance;
        this.userId = userId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subject = subject;
    }
}
