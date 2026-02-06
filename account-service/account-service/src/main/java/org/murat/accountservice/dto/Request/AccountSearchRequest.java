package org.murat.accountservice.dto.Request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.murat.accountservice.entity.AccountType;

@Data
public class AccountSearchRequest {
    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotBlank(message = "Currency is required")
    private String currency;
}
