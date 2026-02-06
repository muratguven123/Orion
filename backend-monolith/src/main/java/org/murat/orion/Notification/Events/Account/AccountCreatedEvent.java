package org.murat.orion.Notification.Events.Account;

import lombok.*;
import org.murat.orion.AccountDomain.Entity.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AccountCreatedEvent {
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private String accountName;
    private String email;
    private String phoneNumber;
    private String subject;
    private AccountType accountType;
    private String currency;
    private BigDecimal initialBalance;
    private LocalDateTime createdAt;

    public AccountCreatedEvent(Long id, Long userId, String accountNumber, String accountName, AccountType accountType, String currency, BigDecimal balance, LocalDateTime createdAt, String email, String phoneNumber, String subject) {
        this.accountId = id;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.accountType = accountType;
        this.currency = currency;
        this.initialBalance = balance;
        this.createdAt = createdAt;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.subject = subject;
    }
}
