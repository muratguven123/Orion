package org.murat.accountservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.accountservice.Event.AccountCreditedEvent;
import org.murat.accountservice.Event.AccountDebitedEvent;
import org.murat.accountservice.Mapper.AccountMapper;
import org.murat.accountservice.dto.Request.AccountSearchRequest;
import org.murat.accountservice.dto.Request.CreateAccountRequest;
import org.murat.accountservice.dto.Request.UpdateAccountRequest;
import org.murat.accountservice.dto.Response.AccountListResponse;
import org.murat.accountservice.dto.Response.AccountResponse;
import org.murat.accountservice.entity.Account;
import org.murat.accountservice.entity.AccountStatus;
import org.murat.accountservice.entity.OutboxEvent;
import org.murat.accountservice.exception.AccessDeniedException;
import org.murat.accountservice.repository.AccountRepository;
import org.murat.accountservice.repository.OutboxEventRepository;
import org.murat.accountservice.specification.AccountSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

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
    public void debit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz Bakiye" + account.getBalance());
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    @Transactional
    public void debitByUserId(Long userId, BigDecimal amount) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        if (accounts.isEmpty()) {
            throw new RuntimeException("Kullanıcıya ait hesap bulunamadı! UserID: " + userId);
        }
        Account account = accounts.get(0);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz Bakiye. Mevcut: " + account.getBalance());
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        try {
            AccountDebitedEvent event = new AccountDebitedEvent(userId, amount, "Hesaptan " + amount + " TL çekildi");
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Account");
            outboxEvent.setAggregateId(account.getId());
            outboxEvent.setEventType("AccountDebitedEvent");
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            outboxEvent.setProcessed(false);
            outboxEventRepository.save(outboxEvent);
            log.info("Account debit outbox event kaydedildi: userId={}, amount={}", userId, amount);
        } catch (JsonProcessingException e) {
            log.error("Account debit event JSON serialize hatası", e);
        }
    }

    @Transactional
    public void creditByUserId(Long userId, BigDecimal amount) {
        List<Account> accounts = accountRepository.findByUserId(userId);

        if (accounts.isEmpty()) {
            throw new RuntimeException("Kullanıcıya ait hesap bulunamadı! UserID: " + userId);
        }
        Account account = accounts.get(0);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        try {
            AccountCreditedEvent event = new AccountCreditedEvent(userId, amount, "Hesaba " + amount + " TL yatırıldı");
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Account");
            outboxEvent.setAggregateId(account.getId());
            outboxEvent.setEventType("AccountCreditedEvent");
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            outboxEvent.setProcessed(false);
            outboxEventRepository.save(outboxEvent);
            log.info("Account credit outbox event kaydedildi: userId={}, amount={}", userId, amount);
        } catch (JsonProcessingException e) {
            log.error("Account credit event JSON serialize hatası", e);
        }
    }
    public boolean hasSufficientBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return account.getBalance().compareTo(amount) >= 0;
    }

    public Page<AccountSearchRequest> searchRequests(AccountSearchRequest request, Pageable pageable) {
        Specification<Account> spec = AccountSpecification.getFilteredPayments(request);
        return accountRepository.findAll(spec, pageable)
                .map(accountMapper::toSearchRequest);
    }




}
