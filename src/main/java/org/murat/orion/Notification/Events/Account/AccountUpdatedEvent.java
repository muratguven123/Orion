package org.murat.orion.Notification.Events.Account;

import lombok.*;
import org.murat.orion.AccountDomain.Entity.AccountType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdatedEvent {
    private Long accountId;
    private Long userId;
    private String accountName;
    private AccountType accountType;
    private String currency;
    private LocalDateTime updatedAt;
}
