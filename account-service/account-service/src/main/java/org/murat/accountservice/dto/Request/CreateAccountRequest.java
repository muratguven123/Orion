package org.murat.accountservice.dto.Request;

import lombok.Data;
import org.murat.accountservice.entity.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotBlank(message = "Currency is required")
    private String currency;

    private BigDecimal initialDeposit;
}
