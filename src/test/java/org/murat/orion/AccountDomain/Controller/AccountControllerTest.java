package org.murat.orion.AccountDomain.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.murat.orion.AccountDomain.Entity.AccountStatus;
import org.murat.orion.AccountDomain.Entity.AccountType;
import org.murat.orion.AccountDomain.Service.AccountService;
import org.murat.orion.AuthDomain.Entity.Role;
import org.murat.orion.AuthDomain.Entity.User;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountController Unit Tests")
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User testUser;
    private AccountResponse testAccountResponse;
    private CreateAccountRequest createAccountRequest;
    private UpdateAccountRequest updateAccountRequest;
    private AccountListResponse accountListResponse;

    private static final Long USER_ID = 1L;
    private static final Long ACCOUNT_ID = 100L;
    private static final String ACCOUNT_NUMBER = "ACC123456789012";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testUser = User.builder()
                .id(USER_ID)
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .isActive(true)
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

        accountListResponse = AccountListResponse.builder()
                .accounts(Arrays.asList(testAccountResponse))
                .totalCount(1)
                .build();
    }

    @Nested
    @DisplayName("POST /api/accounts - Create Account Tests")
    class CreateAccountTests {

        @Test
        @DisplayName("Should create account and return 201")
        void shouldCreateAccountAndReturn201() throws Exception {
            when(accountService.createAccount(any(CreateAccountRequest.class), eq(USER_ID)))
                    .thenReturn(testAccountResponse);

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createAccountRequest))
                            .principal(() -> testUser.getEmail())
                            .requestAttr("org.springframework.security.core.Authentication", testUser))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /api/accounts/{accountId} - Get Account By Id Tests")
    class GetAccountByIdTests {

        @Test
        @DisplayName("Should return account when found")
        void shouldReturnAccountWhenFound() throws Exception {
            when(accountService.getAccountById(eq(ACCOUNT_ID), eq(USER_ID)))
                    .thenReturn(testAccountResponse);

            mockMvc.perform(get("/api/accounts/{accountId}", ACCOUNT_ID)
                            .principal(() -> testUser.getEmail()))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/accounts/number/{accountNumber} - Get Account By Number Tests")
    class GetAccountByNumberTests {

        @Test
        @DisplayName("Should return account when found by number")
        void shouldReturnAccountWhenFoundByNumber() throws Exception {
            when(accountService.getAccountByNumber(eq(ACCOUNT_NUMBER), eq(USER_ID)))
                    .thenReturn(testAccountResponse);

            mockMvc.perform(get("/api/accounts/number/{accountNumber}", ACCOUNT_NUMBER)
                            .principal(() -> testUser.getEmail()))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/accounts/my-accounts - Get My Accounts Tests")
    class GetMyAccountsTests {

        @Test
        @DisplayName("Should return user's accounts")
        void shouldReturnUsersAccounts() throws Exception {
            when(accountService.getAccountsByUserId(eq(USER_ID)))
                    .thenReturn(accountListResponse);

            mockMvc.perform(get("/api/accounts/my-accounts")
                            .principal(() -> testUser.getEmail()))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/accounts/my-accounts/active - Get My Active Accounts Tests")
    class GetMyActiveAccountsTests {

        @Test
        @DisplayName("Should return user's active accounts")
        void shouldReturnUsersActiveAccounts() throws Exception {
            when(accountService.getActiveAccountsByUserId(eq(USER_ID)))
                    .thenReturn(accountListResponse);

            mockMvc.perform(get("/api/accounts/my-accounts/active")
                            .principal(() -> testUser.getEmail()))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PUT /api/accounts/{accountId} - Update Account Tests")
    class UpdateAccountTests {

        @Test
        @DisplayName("Should update account and return 200")
        void shouldUpdateAccountAndReturn200() throws Exception {
            when(accountService.updateAccount(eq(ACCOUNT_ID), any(UpdateAccountRequest.class), eq(USER_ID)))
                    .thenReturn(testAccountResponse);

            mockMvc.perform(put("/api/accounts/{accountId}", ACCOUNT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateAccountRequest))
                            .principal(() -> testUser.getEmail()))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /api/accounts/{accountId}/deactivate - Deactivate Account Tests")
    class DeactivateAccountTests {

        @Test
        @DisplayName("Should deactivate account and return 200")
        void shouldDeactivateAccountAndReturn200() throws Exception {
            AccountResponse deactivatedResponse = AccountResponse.builder()
                    .id(ACCOUNT_ID)
                    .accountNumber(ACCOUNT_NUMBER)
                    .accountName("Test Account")
                    .accountType(AccountType.SAVINGS)
                    .status(AccountStatus.INACTIVE)
                    .isActive(false)
                    .build();

            when(accountService.deactivateAccount(eq(ACCOUNT_ID), eq(USER_ID)))
                    .thenReturn(deactivatedResponse);

            mockMvc.perform(patch("/api/accounts/{accountId}/deactivate", ACCOUNT_ID)
                            .principal(() -> testUser.getEmail()))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PATCH /api/accounts/{accountId}/activate - Activate Account Tests")
    class ActivateAccountTests {

        @Test
        @DisplayName("Should activate account and return 200")
        void shouldActivateAccountAndReturn200() throws Exception {
            when(accountService.activateAccount(eq(ACCOUNT_ID), eq(USER_ID)))
                    .thenReturn(testAccountResponse);

            mockMvc.perform(patch("/api/accounts/{accountId}/activate", ACCOUNT_ID)
                            .principal(() -> testUser.getEmail()))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/accounts/{accountId} - Delete Account Tests")
    class DeleteAccountTests {

        @Test
        @DisplayName("Should delete account and return 204")
        void shouldDeleteAccountAndReturn204() throws Exception {
            doNothing().when(accountService).deleteAccount(eq(ACCOUNT_ID), eq(USER_ID));

            mockMvc.perform(delete("/api/accounts/{accountId}", ACCOUNT_ID)
                            .principal(() -> testUser.getEmail()))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("GET /api/accounts/user/{userId} - Admin Get Accounts By UserId Tests")
    class AdminGetAccountsByUserIdTests {

        @Test
        @DisplayName("Should return accounts for admin")
        void shouldReturnAccountsForAdmin() throws Exception {
            when(accountService.getAccountsByUserId(eq(USER_ID)))
                    .thenReturn(accountListResponse);

            mockMvc.perform(get("/api/accounts/user/{userId}", USER_ID)
                            .principal(() -> "admin@test.com"))
                    .andExpect(status().isOk());
        }
    }
}
