package org.murat.orion.AccountDomain.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.murat.orion.AccountDomain.Dto.Request.CreateAccountRequest;
import org.murat.orion.AccountDomain.Dto.Request.UpdateAccountRequest;
import org.murat.orion.AccountDomain.Dto.Response.AccountListResponse;
import org.murat.orion.AccountDomain.Dto.Response.AccountResponse;
import org.murat.orion.AccountDomain.Service.AccountService;
import org.murat.orion.AuthDomain.Entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request, currentUser.getId(), currentUser.getEmail(), currentUser.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountResponse> getAccountById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long accountId) {
        AccountResponse response = accountService.getAccountById(accountId, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{accountNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String accountNumber) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-accounts")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountListResponse> getMyAccounts(@AuthenticationPrincipal User currentUser) {
        AccountListResponse response = accountService.getAccountsByUserId(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-accounts/active")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountListResponse> getMyActiveAccounts(@AuthenticationPrincipal User currentUser) {
        AccountListResponse response = accountService.getActiveAccountsByUserId(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountResponse> updateAccount(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateAccountRequest request) {
        AccountResponse response = accountService.updateAccount(accountId, request, currentUser.getId(), currentUser.getEmail(), currentUser.getPhoneNumber());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/deactivate")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountResponse> deactivateAccount(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long accountId) {
        AccountResponse response = accountService.deactivateAccount(accountId, currentUser.getId(), currentUser.getEmail(), currentUser.getPhoneNumber());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/activate")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AccountResponse> activateAccount(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long accountId) {
        AccountResponse response = accountService.activateAccount(accountId, currentUser.getId(), currentUser.getEmail(), currentUser.getPhoneNumber());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long accountId) {
        accountService.deleteAccount(accountId, currentUser.getId(), currentUser.getEmail(), currentUser.getPhoneNumber());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<AccountListResponse> getAccountsByUserId(@PathVariable Long userId) {
        AccountListResponse response = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
