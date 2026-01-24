package org.murat.orion.Notification.Events.Account;

import lombok.*;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AccountDeactivatedEvent {
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private String email;
    private String phoneNumber;
    private String reason;
    private LocalDateTime deactivatedAt;

    public AccountDeactivatedEvent(Long id, Long userId, String accountNumber, String userRequestedDeactivation, LocalDateTime updatedAt, String email, String phoneNumber) {
        this.accountId = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.reason = userRequestedDeactivation;
        this.deactivatedAt = updatedAt;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
