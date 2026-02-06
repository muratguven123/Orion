package org.murat.orion.AuthDomain.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.murat.orion.AuthDomain.Dto.Request.EmailLoginRequest;
import org.murat.orion.AuthDomain.Dto.Request.RegisterRequest;
import org.murat.orion.AuthDomain.Dto.Request.SendOtpRequest;
import org.murat.orion.AuthDomain.Dto.Request.VerifyOtpRequest;
import org.murat.orion.AuthDomain.Dto.Response.LoginResponse;
import org.murat.orion.AuthDomain.Dto.Response.OtpResponse;
import org.murat.orion.AuthDomain.Dto.Response.RegisterResponse;
import org.murat.orion.AuthDomain.Service.AuthService;
import org.murat.orion.AuthDomain.Service.EmailLoginStrategy;
import org.murat.orion.AuthDomain.Service.SmsLoginStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private EmailLoginStrategy emailLoginStrategy;

    @Mock
    private SmsLoginStrategy smsLoginStrategy;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private RegisterResponse registerResponse;
    private EmailLoginRequest emailLoginRequest;
    private LoginResponse loginResponse;
    private SendOtpRequest sendOtpRequest;
    private OtpResponse otpResponse;
    private VerifyOtpRequest verifyOtpRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("Murat")
                .lastName("Test")
                .email("murat@test.com")
                .password("password123")
                .phoneNumber("+905551234567")
                .build();

        registerResponse = RegisterResponse.builder()
                .userId(1L)
                .firstName("Murat")
                .lastName("Test")
                .email("murat@test.com")
                .phoneNumber("+905551234567")
                .role("USER")
                .status("SUCCESS")
                .message("Kayıt başarıyla tamamlandı")
                .createdAt(LocalDateTime.now())
                .build();

        emailLoginRequest = EmailLoginRequest.builder()
                .email("murat@test.com")
                .password("password123")
                .build();

        loginResponse = LoginResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .tokenType("Bearer")
                .expiresIn(3600000L)
                .userId(1L)
                .email("murat@test.com")
                .firstName("Murat")
                .lastName("Test")
                .role("USER")
                .phoneNumber("+905551234567")
                .loginTime(LocalDateTime.now())
                .status("SUCCESS")
                .build();

        sendOtpRequest = SendOtpRequest.builder()
                .phoneNumber("+905551234567")
                .build();

        otpResponse = OtpResponse.builder()
                .status("OTP_SENT")
                .message("Doğrulama kodu telefonunuza gönderildi")
                .phoneNumber("+90****67")
                .expiresInSeconds(300)
                .timestamp(LocalDateTime.now())
                .build();

        verifyOtpRequest = VerifyOtpRequest.builder()
                .phoneNumber("+905551234567")
                .verificationCode("123456")
                .build();
    }

    @Nested
    @DisplayName("Register Endpoint Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register user successfully and return CREATED status")
        void register_WithValidRequest_ShouldReturnCreatedStatus() {
            when(authService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

            ResponseEntity<RegisterResponse> result = authController.register(registerRequest);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getUserId()).isEqualTo(1L);
            assertThat(result.getBody().getEmail()).isEqualTo("murat@test.com");
            assertThat(result.getBody().getStatus()).isEqualTo("SUCCESS");

            verify(authService).register(registerRequest);
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void register_WhenEmailExists_ShouldThrowException() {
            when(authService.register(any(RegisterRequest.class)))
                    .thenThrow(new RuntimeException("Bu email adresi zaten kayıtlı"));

            assertThatThrownBy(() -> authController.register(registerRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Bu email adresi zaten kayıtlı");

            verify(authService).register(registerRequest);
        }
    }

    @Nested
    @DisplayName("Email Login Endpoint Tests")
    class EmailLoginTests {

        @Test
        @DisplayName("Should login successfully with email and return OK status")
        void loginWithEmail_WithValidCredentials_ShouldReturnOkStatus() {
            when(emailLoginStrategy.login(any(EmailLoginRequest.class))).thenReturn(loginResponse);

            ResponseEntity<LoginResponse> result = authController.loginWithEmail(emailLoginRequest);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getAccessToken()).isEqualTo("accessToken");
            assertThat(result.getBody().getRefreshToken()).isEqualTo("refreshToken");
            assertThat(result.getBody().getStatus()).isEqualTo("SUCCESS");

            verify(emailLoginStrategy).login(emailLoginRequest);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void loginWithEmail_WhenUserNotFound_ShouldThrowException() {
            when(emailLoginStrategy.login(any(EmailLoginRequest.class)))
                    .thenThrow(new RuntimeException("Kullanıcı bulunamadı"));

            assertThatThrownBy(() -> authController.loginWithEmail(emailLoginRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Kullanıcı bulunamadı");
        }

        @Test
        @DisplayName("Should throw exception when password is wrong")
        void loginWithEmail_WhenPasswordWrong_ShouldThrowException() {
            when(emailLoginStrategy.login(any(EmailLoginRequest.class)))
                    .thenThrow(new RuntimeException("Şifre yanlış"));

            assertThatThrownBy(() -> authController.loginWithEmail(emailLoginRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Şifre yanlış");
        }
    }

    @Nested
    @DisplayName("SMS Send OTP Endpoint Tests")
    class SendOtpTests {

        @Test
        @DisplayName("Should send OTP successfully and return OK status")
        void sendOtp_WithValidPhone_ShouldReturnOkStatus() {
            when(smsLoginStrategy.sendOtp(any(SendOtpRequest.class))).thenReturn(otpResponse);

            ResponseEntity<OtpResponse> result = authController.sendOtp(sendOtpRequest);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getStatus()).isEqualTo("OTP_SENT");
            assertThat(result.getBody().getExpiresInSeconds()).isEqualTo(300);

            verify(smsLoginStrategy).sendOtp(sendOtpRequest);
        }

        @Test
        @DisplayName("Should throw exception when phone not registered")
        void sendOtp_WhenPhoneNotRegistered_ShouldThrowException() {
            when(smsLoginStrategy.sendOtp(any(SendOtpRequest.class)))
                    .thenThrow(new RuntimeException("Bu telefon numarasına kayıtlı kullanıcı bulunamadı"));

            assertThatThrownBy(() -> authController.sendOtp(sendOtpRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Bu telefon numarasına kayıtlı kullanıcı bulunamadı");
        }
    }

    @Nested
    @DisplayName("SMS Verify OTP Endpoint Tests")
    class VerifyOtpTests {

        @Test
        @DisplayName("Should verify OTP and login successfully")
        void verifyOtpAndLogin_WithValidOtp_ShouldReturnOkStatus() {
            when(smsLoginStrategy.verifyOtpAndLogin(any(VerifyOtpRequest.class))).thenReturn(loginResponse);

            ResponseEntity<LoginResponse> result = authController.verifyOtpAndLogin(verifyOtpRequest);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isNotNull();
            assertThat(result.getBody().getAccessToken()).isEqualTo("accessToken");
            assertThat(result.getBody().getStatus()).isEqualTo("SUCCESS");

            verify(smsLoginStrategy).verifyOtpAndLogin(verifyOtpRequest);
        }

        @Test
        @DisplayName("Should throw exception when OTP is invalid")
        void verifyOtpAndLogin_WhenOtpInvalid_ShouldThrowException() {
            when(smsLoginStrategy.verifyOtpAndLogin(any(VerifyOtpRequest.class)))
                    .thenThrow(new RuntimeException("OTP kodu yanlış"));

            assertThatThrownBy(() -> authController.verifyOtpAndLogin(verifyOtpRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("OTP kodu yanlış");
        }

        @Test
        @DisplayName("Should throw exception when OTP is expired")
        void verifyOtpAndLogin_WhenOtpExpired_ShouldThrowException() {
            when(smsLoginStrategy.verifyOtpAndLogin(any(VerifyOtpRequest.class)))
                    .thenThrow(new RuntimeException("OTP geçersiz veya süresi dolmuş"));

            assertThatThrownBy(() -> authController.verifyOtpAndLogin(verifyOtpRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("OTP geçersiz veya süresi dolmuş");
        }

        @Test
        @DisplayName("Should throw exception when max attempts reached")
        void verifyOtpAndLogin_WhenMaxAttemptsReached_ShouldThrowException() {
            when(smsLoginStrategy.verifyOtpAndLogin(any(VerifyOtpRequest.class)))
                    .thenThrow(new RuntimeException("Çok fazla hatalı deneme. Lütfen yeni OTP isteyin."));

            assertThatThrownBy(() -> authController.verifyOtpAndLogin(verifyOtpRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Çok fazla hatalı deneme. Lütfen yeni OTP isteyin.");
        }
    }
}

