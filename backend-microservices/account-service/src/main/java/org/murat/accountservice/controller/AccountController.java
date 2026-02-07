package org.murat.accountservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.murat.accountservice.dto.Request.CreateAccountRequest;
import org.murat.accountservice.dto.Request.UpdateAccountRequest;
import org.murat.accountservice.dto.Response.AccountListResponse;
import org.murat.accountservice.dto.Response.AccountResponse;
import org.murat.accountservice.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/internal/debit")
    public ResponseEntity<Void> debitBalance(@RequestParam("userId") Long userId, @RequestParam("amount") BigDecimal amount) {
        accountService.debitv2(userId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/credit")
    public ResponseEntity<Void> creditBalance(@RequestParam("userId") Long userId, @RequestParam("amount") BigDecimal amount) {
        accountService.credit(userId, amount);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/internal/debitV2")
    public ResponseEntity<Void> debitBalanceV2(
            @RequestParam("userId") Long userId,
            @RequestParam("amount") BigDecimal amount) {
        accountService.debitByUserId(userId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/creditV2")
    public ResponseEntity<Void> creditBalanceV2(
            @RequestParam("userId") Long userId,
            @RequestParam("amount") BigDecimal amount) {

        accountService.creditByUserId(userId, amount);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long accountId) {
        AccountResponse response = accountService.getAccountById(accountId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String accountNumber) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-accounts")
    public ResponseEntity<AccountListResponse> getMyAccounts(@RequestHeader("X-User-Id") Long userId) {
        AccountListResponse response = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-accounts/active")
    public ResponseEntity<AccountListResponse> getMyActiveAccounts(@RequestHeader("X-User-Id") Long userId) {
        AccountListResponse response = accountService.getActiveAccountsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponse> updateAccount(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateAccountRequest request) {
        AccountResponse response = accountService.updateAccount(accountId, request, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/deactivate")
    public ResponseEntity<AccountResponse> deactivateAccount(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long accountId) {
        AccountResponse response = accountService.deactivateAccount(accountId, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/activate")
    public ResponseEntity<AccountResponse> activateAccount(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long accountId) {
        AccountResponse response = accountService.activateAccount(accountId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long accountId) {
        accountService.deleteAccount(accountId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<AccountListResponse> getAccountsByUserId(@PathVariable Long userId) {
        AccountListResponse response = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(response);
    }


}

