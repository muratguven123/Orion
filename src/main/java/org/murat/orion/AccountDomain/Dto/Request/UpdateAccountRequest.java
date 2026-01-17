package org.murat.orion.AccountDomain.Dto.Request;

import org.murat.orion.AccountDomain.Entity.AccountType;
import org.murat.orion.AccountDomain.Entity.AccountType;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateAccountRequest {
    private String currency;

    private AccountType accountType;

    private String accountName;
}