package org.murat.orion.Notification.Events.Account;

import lombok.*;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AccountActivatedEvent {
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private LocalDateTime activatedAt;

    public AccountActivatedEvent(Long accountId, String accountNumber, LocalDateTime activatedAt, Long userId) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.activatedAt = activatedAt;
        this.userId = userId;
    }
}
