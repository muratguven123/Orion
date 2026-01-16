package org.murat.orion.AuthDomain.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.murat.orion.AuthDomain.Config.JwtService;
import org.murat.orion.AuthDomain.Dto.Request.EmailLoginRequest;
import org.murat.orion.AuthDomain.Dto.Response.LoginResponse;
import org.murat.orion.AuthDomain.Entity.Role;
import org.murat.orion.AuthDomain.Entity.User;
import org.murat.orion.AuthDomain.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailLoginStrategy Unit Tests")
class EmailLoginStrategyTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private EmailLoginStrategy emailLoginStrategy;

    private EmailLoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        loginRequest = EmailLoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .firstName("Murat")
                .lastName("Test")
                .email("test@example.com")
                .password("encodedPassword123")
                .phoneNumber("+905551234567")
                .role(Role.USER)
                .isActive(true)
                .isEmailVerified(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with correct email and password")
        void login_WithValidCredentials_ShouldReturnLoginResponse() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
            when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
            when(jwtService.getExpirationTime()).thenReturn(3600000L);

            LoginResponse result = emailLoginStrategy.login(loginRequest);

            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo("accessToken");
            assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
            assertThat(result.getTokenType()).isEqualTo("Bearer");
            assertThat(result.getUserId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            assertThat(result.getFirstName()).isEqualTo("Murat");
            assertThat(result.getLastName()).isEqualTo("Test");
            assertThat(result.getRole()).isEqualTo("USER");
            assertThat(result.getStatus()).isEqualTo("SUCCESS");

            verify(userRepository).findByEmail("test@example.com");
            verify(passwordEncoder).matches("password123", "encodedPassword123");
            verify(jwtService).generateToken(user);
            verify(jwtService).generateRefreshToken(user);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void login_WhenUserNotFound_ShouldThrowException() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> emailLoginStrategy.login(loginRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Kullanıcı bulunamadı");

            verify(userRepository).findByEmail("test@example.com");
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtService, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when password is incorrect")
        void login_WhenPasswordIncorrect_ShouldThrowException() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(false);

            assertThatThrownBy(() -> emailLoginStrategy.login(loginRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Şifre yanlış");

            verify(userRepository).findByEmail("test@example.com");
            verify(passwordEncoder).matches("password123", "encodedPassword123");
            verify(jwtService, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("Should return correct login type")
        void getLoginType_ShouldReturnEmail() {
            String loginType = emailLoginStrategy.getLoginType();

            assertThat(loginType).isEqualTo("EMAIL");
        }

        @Test
        @DisplayName("Should set login time in response")
        void login_ShouldSetLoginTime() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
            when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
            when(jwtService.getExpirationTime()).thenReturn(3600000L);

            LoginResponse result = emailLoginStrategy.login(loginRequest);

            assertThat(result.getLoginTime()).isNotNull();
            assertThat(result.getLoginTime()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("Should include phone number in response")
        void login_ShouldIncludePhoneNumber() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
            when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
            when(jwtService.getExpirationTime()).thenReturn(3600000L);

            LoginResponse result = emailLoginStrategy.login(loginRequest);

            assertThat(result.getPhoneNumber()).isEqualTo("+905551234567");
        }

        @Test
        @DisplayName("Should include expiration time in response")
        void login_ShouldIncludeExpirationTime() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
            when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
            when(jwtService.getExpirationTime()).thenReturn(3600000L);

            LoginResponse result = emailLoginStrategy.login(loginRequest);

            assertThat(result.getExpiresIn()).isEqualTo(3600000L);
        }
    }
}

