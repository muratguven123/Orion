package org.murat.orion.AccountDomain.Mapper;

import org.murat.orion.AccountDomain.Dto.Request.AccountSearchRequest;
import org.murat.orion.AccountDomain.Dto.Request.CreateAccountRequest;
import org.murat.orion.AccountDomain.Dto.Response.AccountResponse;
import org.murat.orion.AccountDomain.Entity.Account;
import org.murat.orion.AccountDomain.Entity.AccountStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class AccountMapper {

    public Account toEntity(CreateAccountRequest request, Long userId) {
        return Account.builder()
                .userId(userId)
                .accountNumber(generateAccountNumber())
                .accountName(request.getAccountName())
                .accountType(request.getAccountType())
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .currency(request.getCurrency())
                .isActive(true)
                .build();
    }

    public AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .isActive(account.getIsActive())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    public AccountSearchRequest toSearchRequest(Account account) {
        AccountSearchRequest searchRequest = new AccountSearchRequest();
        searchRequest.setAccountName(account.getAccountName());
        searchRequest.setAccountType(account.getAccountType());
        searchRequest.setCurrency(account.getCurrency());
        return searchRequest;
    }
}
