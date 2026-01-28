package org.murat.orion.AccountDomain.Service;

import lombok.RequiredArgsConstructor;
import org.murat.orion.AccountDomain.Dto.Request.AccountSearchRequest;
import org.murat.orion.AccountDomain.Dto.Request.CreateAccountRequest;
import org.murat.orion.AccountDomain.Dto.Request.UpdateAccountRequest;
import org.murat.orion.AccountDomain.Dto.Response.AccountListResponse;
import org.murat.orion.AccountDomain.Dto.Response.AccountResponse;
import org.murat.orion.AccountDomain.Entity.Account;
import org.murat.orion.AccountDomain.Entity.AccountStatus;
import org.murat.orion.AccountDomain.Mapper.AccountMapper;
import org.murat.orion.AccountDomain.Repository.AccountRepository;
import org.murat.orion.AccountDomain.Specification.AccountSpecification;
import org.murat.orion.Notification.Events.Account.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableAsync
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, Long userId, String email, String phoneNumber) {
        Account account = accountMapper.toEntity(request, userId);
        Account savedAccount = accountRepository.save(account);
        AccountCreatedEvent event = new AccountCreatedEvent(
                savedAccount.getId(),
                savedAccount.getUserId(),
                savedAccount.getAccountNumber(),
                savedAccount.getAccountName(),
                savedAccount.getAccountType(),
                savedAccount.getCurrency(),
                savedAccount.getBalance(),
                savedAccount.getCreatedAt(),
                email,
                phoneNumber,
                "Hesap Oluşturuldu"
        );
        applicationEventPublisher.publishEvent(event);
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
    public AccountResponse updateAccount(Long accountId, UpdateAccountRequest request, Long currentUserId, String email, String phoneNumber) {
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
        AccountUpdatedEvent event = AccountUpdatedEvent.builder()
                .accountId(accountId)
                .userId(currentUserId)
                .accountName(updatedAccount.getAccountName())
                .accountType(updatedAccount.getAccountType())
                .currency(updatedAccount.getCurrency())
                .updatedAt(updatedAccount.getUpdatedAt())
                .email(email)
                .phoneNumber(phoneNumber)
                .subject("Hesap Güncellendi")
                .build();
        applicationEventPublisher.publishEvent(event);
        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse deactivateAccount(Long accountId, Long currentUserId, String email, String phoneNumber) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        validateAccountOwnership(account, currentUserId);
        account.setIsActive(false);
        account.setStatus(AccountStatus.INACTIVE);
        Account updatedAccount = accountRepository.save(account);
        AccountDeactivatedEvent event = new AccountDeactivatedEvent(
                updatedAccount.getId(),
                updatedAccount.getUserId(),
                updatedAccount.getAccountNumber(),
                "User requested deactivation",
                updatedAccount.getUpdatedAt(),
                email,
                phoneNumber,
                "Hesap Devre Dışı Bırakıldı"
        );
        applicationEventPublisher.publishEvent(event);
        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public AccountResponse activateAccount(Long accountId, Long currentUserId, String email, String phoneNumber) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        validateAccountOwnership(account, currentUserId);
        account.setIsActive(true);
        account.setStatus(AccountStatus.ACTIVE);
        Account updatedAccount = accountRepository.save(account);
        AccountActivatedEvent event = new AccountActivatedEvent(
                updatedAccount.getId(),
                updatedAccount.getAccountNumber(),
                updatedAccount.getUpdatedAt(),
                updatedAccount.getUserId(),
                email,
                phoneNumber,
                "Hesap Aktif Edildi"
        );
        applicationEventPublisher.publishEvent(event);
        return accountMapper.toResponse(updatedAccount);
    }

    @Transactional
    public void deleteAccount(Long accountId, Long currentUserId, String email, String phoneNumber) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        validateAccountOwnership(account, currentUserId);
        account.setStatus(AccountStatus.CLOSED);
        account.setIsActive(false);
        accountRepository.save(account);
        AccountDeletedEvent event = new AccountDeletedEvent(
                account.getId(),
                account.getAccountNumber(),
                account.getUpdatedAt(),
                account.getBalance(),
                account.getUserId(),
                email,
                phoneNumber,
                "Hesap Silindi"
        );
        applicationEventPublisher.publishEvent(event);

    }

    private void validateAccountOwnership(Account account, Long currentUserId) {
        if (!account.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Bu hesaba erişim yetkiniz yok");
        }
    }
    @Transactional
    public void debit(Long accountId, BigDecimal amount, String email, String phoneNumber){
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new RuntimeException("Account not found"));

        if(account.getBalance().compareTo(amount) < 0){
            throw new RuntimeException("Yetersiz Bakiye"+account.getBalance());
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        AccountDebitedEvent event = new AccountDebitedEvent(
                account.getId(),
                amount,
                account.getCurrency(),
                java.time.LocalDateTime.now(),
                account.getBalance(),
                account.getBalance().add(amount),
                UUID.randomUUID().toString(),
                email,
                phoneNumber,
                "Hesaptan Para Çekildi"
        );
        applicationEventPublisher.publishEvent(event);

    }
    @Transactional
    public void credit(Long accountId, BigDecimal amount, String email, String phoneNumber){
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        AccountCreditedEvent event = new AccountCreditedEvent(
                account.getId(),
                amount,
                java.time.LocalDateTime.now(),
                account.getCurrency(),
                account.getBalance(),
                account.getBalance().subtract(amount),
                UUID.randomUUID().toString(),
                email,
                phoneNumber,
                "Hesaba Para Yatırıldı"
        );
        applicationEventPublisher.publishEvent(event);
    }
    public boolean hasSufficientBalance(Long accountId, BigDecimal amount){
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new RuntimeException("Account not found"));
        return account.getBalance().compareTo(amount) >= 0;
    }
    public Page<AccountSearchRequest> searchRequests(AccountSearchRequest request, Pageable pageable) {
        Specification<Account> spec = AccountSpecification.getFilteredPayments(request);
        return accountRepository.findAll(spec, pageable)
                .map(accountMapper::toSearchRequest);
    }
}
