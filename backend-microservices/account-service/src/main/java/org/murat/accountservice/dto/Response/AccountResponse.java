package org.murat.accountservice.dto.Response;

import lombok.Builder;
import lombok.Data;
import org.murat.accountservice.entity.AccountStatus;
import org.murat.accountservice.entity.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private String accountName;
    private AccountType accountType;
    private AccountStatus status;
    private BigDecimal balance;
    private String currency;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
