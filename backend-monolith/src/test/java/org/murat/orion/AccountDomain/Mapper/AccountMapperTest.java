package org.murat.orion.AccountDomain.Mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.murat.orion.AccountDomain.Dto.Request.CreateAccountRequest;
import org.murat.orion.AccountDomain.Dto.Response.AccountResponse;
import org.murat.orion.AccountDomain.Entity.Account;
import org.murat.orion.AccountDomain.Entity.AccountStatus;
import org.murat.orion.AccountDomain.Entity.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccountMapper Unit Tests")
class AccountMapperTest {

    private AccountMapper accountMapper;

    private static final Long USER_ID = 1L;
    private static final Long ACCOUNT_ID = 100L;
    private static final String ACCOUNT_NUMBER = "ACC123456789012";

    @BeforeEach
    void setUp() {
        accountMapper = new AccountMapper();
    }

    @Nested
    @DisplayName("toEntity Tests")
    class ToEntityTests {

        @Test
        @DisplayName("Should map CreateAccountRequest to Account entity")
        void shouldMapCreateAccountRequestToAccountEntity() {
            CreateAccountRequest request = CreateAccountRequest.builder()
                    .accountName("My Savings Account")
                    .accountType(AccountType.SAVINGS)
                    .currency("TRY")
                    .build();

            Account account = accountMapper.toEntity(request, USER_ID);

            assertThat(account).isNotNull();
            assertThat(account.getUserId()).isEqualTo(USER_ID);
            assertThat(account.getAccountName()).isEqualTo("My Savings Account");
            assertThat(account.getAccountType()).isEqualTo(AccountType.SAVINGS);
            assertThat(account.getCurrency()).isEqualTo("TRY");
            assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
            assertThat(account.getBalance()).isEqualTo(BigDecimal.ZERO);
            assertThat(account.getIsActive()).isTrue();
        }

        @Test
        @DisplayName("Should generate account number starting with ACC")
        void shouldGenerateAccountNumberStartingWithACC() {
            CreateAccountRequest request = CreateAccountRequest.builder()
                    .accountName("Test Account")
                    .accountType(AccountType.CHECKING)
                    .currency("USD")
                    .build();

            Account account = accountMapper.toEntity(request, USER_ID);

            assertThat(account.getAccountNumber()).isNotNull();
            assertThat(account.getAccountNumber()).startsWith("ACC");
            assertThat(account.getAccountNumber()).hasSize(15);
        }

        @Test
        @DisplayName("Should set default values for new account")
        void shouldSetDefaultValuesForNewAccount() {
            CreateAccountRequest request = CreateAccountRequest.builder()
                    .accountName("Default Test")
                    .accountType(AccountType.DEPOSIT)
                    .currency("EUR")
                    .build();

            Account account = accountMapper.toEntity(request, USER_ID);

            assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
            assertThat(account.getBalance()).isEqualTo(BigDecimal.ZERO);
            assertThat(account.getIsActive()).isTrue();
        }

        @Test
        @DisplayName("Should generate unique account numbers")
        void shouldGenerateUniqueAccountNumbers() {
            CreateAccountRequest request = CreateAccountRequest.builder()
                    .accountName("Test")
                    .accountType(AccountType.SAVINGS)
                    .currency("TRY")
                    .build();

            Account account1 = accountMapper.toEntity(request, USER_ID);
            Account account2 = accountMapper.toEntity(request, USER_ID);

            assertThat(account1.getAccountNumber()).isNotEqualTo(account2.getAccountNumber());
        }
    }

    @Nested
    @DisplayName("toResponse Tests")
    class ToResponseTests {

        @Test
        @DisplayName("Should map Account entity to AccountResponse")
        void shouldMapAccountEntityToAccountResponse() {
            LocalDateTime now = LocalDateTime.now();
            Account account = Account.builder()
                    .id(ACCOUNT_ID)
                    .userId(USER_ID)
                    .accountNumber(ACCOUNT_NUMBER)
                    .accountName("Test Account")
                    .accountType(AccountType.SAVINGS)
                    .status(AccountStatus.ACTIVE)
                    .balance(BigDecimal.valueOf(5000))
                    .currency("TRY")
                    .isActive(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            AccountResponse response = accountMapper.toResponse(account);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(ACCOUNT_ID);
            assertThat(response.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
            assertThat(response.getAccountName()).isEqualTo("Test Account");
            assertThat(response.getAccountType()).isEqualTo(AccountType.SAVINGS);
            assertThat(response.getStatus()).isEqualTo(AccountStatus.ACTIVE);
            assertThat(response.getBalance()).isEqualTo(BigDecimal.valueOf(5000));
            assertThat(response.getCurrency()).isEqualTo("TRY");
            assertThat(response.getIsActive()).isTrue();
            assertThat(response.getCreatedAt()).isEqualTo(now);
            assertThat(response.getUpdatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Should map inactive account correctly")
        void shouldMapInactiveAccountCorrectly() {
            Account account = Account.builder()
                    .id(ACCOUNT_ID)
                    .userId(USER_ID)
                    .accountNumber(ACCOUNT_NUMBER)
                    .accountName("Inactive Account")
                    .accountType(AccountType.CHECKING)
                    .status(AccountStatus.INACTIVE)
                    .balance(BigDecimal.valueOf(100))
                    .currency("USD")
                    .isActive(false)
                    .build();

            AccountResponse response = accountMapper.toResponse(account);

            assertThat(response.getIsActive()).isFalse();
            assertThat(response.getStatus()).isEqualTo(AccountStatus.INACTIVE);
        }

        @Test
        @DisplayName("Should map closed account correctly")
        void shouldMapClosedAccountCorrectly() {
            Account account = Account.builder()
                    .id(ACCOUNT_ID)
                    .userId(USER_ID)
                    .accountNumber(ACCOUNT_NUMBER)
                    .accountName("Closed Account")
                    .accountType(AccountType.INVESTMENT)
                    .status(AccountStatus.CLOSED)
                    .balance(BigDecimal.ZERO)
                    .currency("EUR")
                    .isActive(false)
                    .build();

            AccountResponse response = accountMapper.toResponse(account);

            assertThat(response.getStatus()).isEqualTo(AccountStatus.CLOSED);
            assertThat(response.getIsActive()).isFalse();
        }

        @Test
        @DisplayName("Should handle null timestamps")
        void shouldHandleNullTimestamps() {
            Account account = Account.builder()
                    .id(ACCOUNT_ID)
                    .userId(USER_ID)
                    .accountNumber(ACCOUNT_NUMBER)
                    .accountName("No Timestamps")
                    .accountType(AccountType.SAVINGS)
                    .status(AccountStatus.ACTIVE)
                    .balance(BigDecimal.ZERO)
                    .currency("TRY")
                    .isActive(true)
                    .createdAt(null)
                    .updatedAt(null)
                    .build();

            AccountResponse response = accountMapper.toResponse(account);

            assertThat(response.getCreatedAt()).isNull();
            assertThat(response.getUpdatedAt()).isNull();
        }

        @Test
        @DisplayName("Should map all account types correctly")
        void shouldMapAllAccountTypesCorrectly() {
            for (AccountType type : AccountType.values()) {
                Account account = Account.builder()
                        .id(ACCOUNT_ID)
                        .userId(USER_ID)
                        .accountNumber(ACCOUNT_NUMBER)
                        .accountName("Type Test")
                        .accountType(type)
                        .status(AccountStatus.ACTIVE)
                        .balance(BigDecimal.ZERO)
                        .currency("TRY")
                        .isActive(true)
                        .build();

                AccountResponse response = accountMapper.toResponse(account);

                assertThat(response.getAccountType()).isEqualTo(type);
            }
        }
    }
}
