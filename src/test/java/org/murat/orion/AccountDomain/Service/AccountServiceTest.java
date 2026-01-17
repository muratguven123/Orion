package org.murat.orion.AccountDomain.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.murat.orion.AccountDomain.Dto.Request.CreateAccountRequest;
import org.murat.orion.AccountDomain.Dto.Request.UpdateAccountRequest;
import org.murat.orion.AccountDomain.Dto.Response.AccountListResponse;
import org.murat.orion.AccountDomain.Dto.Response.AccountResponse;
import org.murat.orion.AccountDomain.Entity.Account;
import org.murat.orion.AccountDomain.Entity.AccountStatus;
import org.murat.orion.AccountDomain.Entity.AccountType;
import org.murat.orion.AccountDomain.Mapper.AccountMapper;
import org.murat.orion.AccountDomain.Repository.AccountRepository;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Unit Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private AccountResponse testAccountResponse;
    private CreateAccountRequest createAccountRequest;
    private UpdateAccountRequest updateAccountRequest;

    private static final Long USER_ID = 1L;
    private static final Long ACCOUNT_ID = 100L;
    private static final String ACCOUNT_NUMBER = "ACC123456789012";

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(ACCOUNT_ID)
                .userId(USER_ID)
                .accountNumber(ACCOUNT_NUMBER)
                .accountName("Test Account")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .currency("TRY")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testAccountResponse = AccountResponse.builder()
                .id(ACCOUNT_ID)
                .accountNumber(ACCOUNT_NUMBER)
                .accountName("Test Account")
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .currency("TRY")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createAccountRequest = CreateAccountRequest.builder()
                .accountName("Test Account")
                .accountType(AccountType.SAVINGS)
                .currency("TRY")
                .build();

        updateAccountRequest = UpdateAccountRequest.builder()
                .accountName("Updated Account")
                .accountType(AccountType.CHECKING)
                .currency("USD")
                .build();
    }

    @Nested
    @DisplayName("createAccount Tests")
    class CreateAccountTests {

        @Test
        @DisplayName("Should create account successfully")
        void shouldCreateAccountSuccessfully() {
            when(accountMapper.toEntity(createAccountRequest, USER_ID)).thenReturn(testAccount);
            when(accountRepository.save(testAccount)).thenReturn(testAccount);
            when(accountMapper.toResponse(testAccount)).thenReturn(testAccountResponse);

            AccountResponse response = accountService.createAccount(createAccountRequest, USER_ID);

            assertThat(response).isNotNull();
            assertThat(response.getAccountName()).isEqualTo("Test Account");
            assertThat(response.getAccountType()).isEqualTo(AccountType.SAVINGS);
            verify(accountRepository, times(1)).save(testAccount);
        }
    }

    @Nested
    @DisplayName("getAccountById Tests")
    class GetAccountByIdTests {

        @Test
        @DisplayName("Should return account when found and user is owner")
        void shouldReturnAccountWhenFoundAndUserIsOwner() {
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
            when(accountMapper.toResponse(testAccount)).thenReturn(testAccountResponse);

            AccountResponse response = accountService.getAccountById(ACCOUNT_ID, USER_ID);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(ACCOUNT_ID);
            verify(accountRepository, times(1)).findById(ACCOUNT_ID);
        }

        @Test
        @DisplayName("Should throw exception when account not found")
        void shouldThrowExceptionWhenAccountNotFound() {
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.getAccountById(ACCOUNT_ID, USER_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Account not found");
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user is not owner")
        void shouldThrowAccessDeniedExceptionWhenUserIsNotOwner() {
            Long differentUserId = 999L;
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

            assertThatThrownBy(() -> accountService.getAccountById(ACCOUNT_ID, differentUserId))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("Bu hesaba erişim yetkiniz yok");
        }
    }

    @Nested
    @DisplayName("getAccountByNumber Tests")
    class GetAccountByNumberTests {

        @Test
        @DisplayName("Should return account when found by account number")
        void shouldReturnAccountWhenFoundByAccountNumber() {
            when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(testAccount));
            when(accountMapper.toResponse(testAccount)).thenReturn(testAccountResponse);

            AccountResponse response = accountService.getAccountByNumber(ACCOUNT_NUMBER, USER_ID);

            assertThat(response).isNotNull();
            assertThat(response.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
        }

        @Test
        @DisplayName("Should throw exception when account number not found")
        void shouldThrowExceptionWhenAccountNumberNotFound() {
            when(accountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.getAccountByNumber(ACCOUNT_NUMBER, USER_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Account not found");
        }
    }

    @Nested
    @DisplayName("getAccountsByUserId Tests")
    class GetAccountsByUserIdTests {

        @Test
        @DisplayName("Should return list of accounts for user")
        void shouldReturnListOfAccountsForUser() {
            List<Account> accounts = Arrays.asList(testAccount);
            when(accountRepository.findByUserId(USER_ID)).thenReturn(accounts);
            when(accountMapper.toResponse(testAccount)).thenReturn(testAccountResponse);

            AccountListResponse response = accountService.getAccountsByUserId(USER_ID);

            assertThat(response).isNotNull();
            assertThat(response.getAccounts()).hasSize(1);
            assertThat(response.getTotalCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should return empty list when user has no accounts")
        void shouldReturnEmptyListWhenUserHasNoAccounts() {
            when(accountRepository.findByUserId(USER_ID)).thenReturn(List.of());

            AccountListResponse response = accountService.getAccountsByUserId(USER_ID);

            assertThat(response).isNotNull();
            assertThat(response.getAccounts()).isEmpty();
            assertThat(response.getTotalCount()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("getActiveAccountsByUserId Tests")
    class GetActiveAccountsByUserIdTests {

        @Test
        @DisplayName("Should return only active accounts for user")
        void shouldReturnOnlyActiveAccountsForUser() {
            List<Account> activeAccounts = Arrays.asList(testAccount);
            when(accountRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(activeAccounts);
            when(accountMapper.toResponse(testAccount)).thenReturn(testAccountResponse);

            AccountListResponse response = accountService.getActiveAccountsByUserId(USER_ID);

            assertThat(response).isNotNull();
            assertThat(response.getAccounts()).hasSize(1);
            assertThat(response.getAccounts().get(0).getIsActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("updateAccount Tests")
    class UpdateAccountTests {

        @Test
        @DisplayName("Should update account successfully")
        void shouldUpdateAccountSuccessfully() {
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
            when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
            when(accountMapper.toResponse(any(Account.class))).thenReturn(testAccountResponse);

            AccountResponse response = accountService.updateAccount(ACCOUNT_ID, updateAccountRequest, USER_ID);

            assertThat(response).isNotNull();
            verify(accountRepository, times(1)).save(any(Account.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent account")
        void shouldThrowExceptionWhenUpdatingNonExistentAccount() {
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.updateAccount(ACCOUNT_ID, updateAccountRequest, USER_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Account not found");
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when updating other user's account")
        void shouldThrowAccessDeniedExceptionWhenUpdatingOtherUsersAccount() {
            Long differentUserId = 999L;
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

            assertThatThrownBy(() -> accountService.updateAccount(ACCOUNT_ID, updateAccountRequest, differentUserId))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            UpdateAccountRequest partialUpdate = UpdateAccountRequest.builder()
                    .accountName("New Name Only")
                    .build();

            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
            when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
            when(accountMapper.toResponse(any(Account.class))).thenReturn(testAccountResponse);

            accountService.updateAccount(ACCOUNT_ID, partialUpdate, USER_ID);

            verify(accountRepository).save(argThat(account ->
                account.getAccountName().equals("New Name Only")
            ));
        }
    }

    @Nested
    @DisplayName("deactivateAccount Tests")
    class DeactivateAccountTests {

        @Test
        @DisplayName("Should deactivate account successfully")
        void shouldDeactivateAccountSuccessfully() {
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
            when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
            when(accountMapper.toResponse(any(Account.class))).thenReturn(testAccountResponse);

            AccountResponse response = accountService.deactivateAccount(ACCOUNT_ID, USER_ID);

            assertThat(response).isNotNull();
            verify(accountRepository).save(argThat(account ->
                !account.getIsActive() && account.getStatus() == AccountStatus.INACTIVE
            ));
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when deactivating other user's account")
        void shouldThrowAccessDeniedExceptionWhenDeactivatingOtherUsersAccount() {
            Long differentUserId = 999L;
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

            assertThatThrownBy(() -> accountService.deactivateAccount(ACCOUNT_ID, differentUserId))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("activateAccount Tests")
    class ActivateAccountTests {

        @Test
        @DisplayName("Should activate account successfully")
        void shouldActivateAccountSuccessfully() {
            testAccount.setIsActive(false);
            testAccount.setStatus(AccountStatus.INACTIVE);

            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
            when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
            when(accountMapper.toResponse(any(Account.class))).thenReturn(testAccountResponse);

            AccountResponse response = accountService.activateAccount(ACCOUNT_ID, USER_ID);

            assertThat(response).isNotNull();
            verify(accountRepository).save(argThat(account ->
                account.getIsActive() && account.getStatus() == AccountStatus.ACTIVE
            ));
        }
    }

    @Nested
    @DisplayName("deleteAccount Tests")
    class DeleteAccountTests {

        @Test
        @DisplayName("Should soft delete account successfully")
        void shouldSoftDeleteAccountSuccessfully() {
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
            when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

            accountService.deleteAccount(ACCOUNT_ID, USER_ID);

            verify(accountRepository).save(argThat(account ->
                !account.getIsActive() && account.getStatus() == AccountStatus.CLOSED
            ));
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent account")
        void shouldThrowExceptionWhenDeletingNonExistentAccount() {
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.deleteAccount(ACCOUNT_ID, USER_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Account not found");
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when deleting other user's account")
        void shouldThrowAccessDeniedExceptionWhenDeletingOtherUsersAccount() {
            Long differentUserId = 999L;
            when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

            assertThatThrownBy(() -> accountService.deleteAccount(ACCOUNT_ID, differentUserId))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }
}
