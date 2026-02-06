package org.murat.accountservice.dto.Request;

import lombok.Data;
import org.murat.accountservice.entity.AccountType;

@Data
public class UpdateAccountRequest {
    private String accountName;
    private AccountType accountType;
    private String currency;
}
