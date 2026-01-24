package org.murat.orion.Notification.Events.Account;

import lombok.*;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AccountActivatedEvent {
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private String email;
    private String phoneNumber;
    private String subject;
    private LocalDateTime activatedAt;

    public AccountActivatedEvent(Long accountId, String accountNumber, LocalDateTime activatedAt, Long userId, String email, String phoneNumber, String subject) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.activatedAt = activatedAt;
        this.userId = userId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subject = subject;
    }
}
