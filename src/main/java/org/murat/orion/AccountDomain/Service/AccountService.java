package org.murat.orion.AccountDomain.Service;

import lombok.RequiredArgsConstructor;
import org.murat.orion.AccountDomain.Dto.Request.CreateAccountRequest;
import org.murat.orion.AccountDomain.Dto.Request.UpdateAccountRequest;
import org.murat.orion.AccountDomain.Dto.Response.AccountListResponse;
import org.murat.orion.AccountDomain.Dto.Response.AccountResponse;
import org.murat.orion.AccountDomain.Entity.Account;
import org.murat.orion.AccountDomain.Entity.AccountStatus;
import org.murat.orion.AccountDomain.Mapper.AccountMapper;
import org.murat.orion.AccountDomain.Repository.AccountRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, Long userId) {
        Account account = accountMapper.toEntity(request, userId);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponse(savedAccount);
    }

    public AccountResponse getAccountById(Long accountId, Long currentUserId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        validateAccountOwnership(account, currentUserId);
        return accountMapper.toResponse(account);
    }

    public AccountResponse getAccountByNumber(String accountNumber, Long currentUserId) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        validateAccountOwnership(account, currentUserId);
        return accountMapper.toResponse(account);
    }

    public AccountListResponse getAccountsByUserId(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        List<AccountResponse> accountResponses = accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
        return AccountListResponse.builder()
                .accounts(accountResponses)
                .totalCount(accountResponses.size())
                .build();
    }

    public AccountListResponse getActiveAccountsByUserId(Long userId) {
        List<Account> accounts = accountRepository.findByUserIdAndIsActiveTrue(userId);
        List<AccountResponse> accountResponses = accounts.stream()
                .map(accountMapper::toResponse)
                .collect(Collectors.toList());
        return AccountListResponse.builder()
                .accounts(accountResponses)
                .totalCount(accountResponses.size())
                .build();
    }

    @Transactional
    public AccountResponse updateAccount(Long accountId, UpdateAccountRequest request, Long currentUserId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        validateAccountOwnership(account, currentUserId);

        if (request.getAccountName() != null) {
            account.setAccountName(request.getAccountName());
        }
        if (request.getAccountType() != null) {
            account.setAccountType(request.getAccountType());
        }
        if (request.getCurrency() != null) {
            account.setCurrency(request.getCurrency());
        }

        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse deactivateAccount(Long accountId, Long currentUserId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        validateAccountOwnership(account, currentUserId);
        account.setIsActive(false);
        account.setStatus(AccountStatus.INACTIVE);
        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse activateAccount(Long accountId, Long currentUserId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        validateAccountOwnership(account, currentUserId);
        account.setIsActive(true);
        account.setStatus(AccountStatus.ACTIVE);
        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public void deleteAccount(Long accountId, Long currentUserId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        validateAccountOwnership(account, currentUserId);
        account.setStatus(AccountStatus.CLOSED);
        account.setIsActive(false);
        accountRepository.save(account);
    }

    private void validateAccountOwnership(Account account, Long currentUserId) {
        if (!account.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Bu hesaba erişim yetkiniz yok");
        }
    }
    @Transactional
    public void debit(Long accountId, BigDecimal amount){
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new RuntimeException("Account not found"));

        if(account.getBalance().compareTo(amount) < 0){
            throw new RuntimeException("Yetersiz Bakiye"+account.getBalance());
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }
    @Transactional
    public void credit(Long accountId, BigDecimal amount){
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }
    public boolean hasSufficientBalance(Long accountId, BigDecimal amount){
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new RuntimeException("Account not found"));
        return account.getBalance().compareTo(amount) >= 0;
    }
}
