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
import org.murat.orion.AuthDomain.Dto.Request.SendOtpRequest;
import org.murat.orion.AuthDomain.Dto.Request.VerifyOtpRequest;
import org.murat.orion.AuthDomain.Dto.Response.LoginResponse;
import org.murat.orion.AuthDomain.Dto.Response.OtpResponse;
import org.murat.orion.AuthDomain.Entity.Role;
import org.murat.orion.AuthDomain.Entity.User;
import org.murat.orion.AuthDomain.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmsLoginStrategy Unit Tests")
class SmsLoginStrategyTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private SmsService smsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private SmsLoginStrategy smsLoginStrategy;

    private User user;
    private SendOtpRequest sendOtpRequest;
    private VerifyOtpRequest verifyOtpRequest;

    private static final String PHONE_NUMBER = "+905551234567";
    private static final String OTP_CODE = "123456";

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Murat")
                .lastName("Test")
                .email("murat@test.com")
                .password("encodedPassword123")
                .phoneNumber(PHONE_NUMBER)
                .role(Role.USER)
                .isActive(true)
                .isEmailVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

        sendOtpRequest = SendOtpRequest.builder()
                .phoneNumber(PHONE_NUMBER)
                .build();

        verifyOtpRequest = VerifyOtpRequest.builder()
                .phoneNumber(PHONE_NUMBER)
                .verificationCode(OTP_CODE)
                .build();
    }

    @Nested
    @DisplayName("Send OTP Tests")
    class SendOtpTests {

        @Test
        @DisplayName("Should send OTP successfully for registered user")
        void sendOtp_ForRegisteredUser_ShouldReturnOtpResponse() {
            when(userRepository.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(user));
            when(otpService.generateOtp(PHONE_NUMBER)).thenReturn(OTP_CODE);
            doNothing().when(smsService).sendOtp(anyString(), anyString());

            OtpResponse result = smsLoginStrategy.sendOtp(sendOtpRequest);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo("OTP_SENT");
            assertThat(result.getMessage()).isEqualTo("Doğrulama kodu telefonunuza gönderildi");
            assertThat(result.getExpiresInSeconds()).isEqualTo(300);
            assertThat(result.getTimestamp()).isNotNull();
            assertThat(result.getPhoneNumber()).contains("****");

            verify(userRepository).findByPhoneNumber(PHONE_NUMBER);
            verify(otpService).generateOtp(PHONE_NUMBER);
            verify(smsService).sendOtp(PHONE_NUMBER, OTP_CODE);
        }

        @Test
        @DisplayName("Should throw exception when phone number not registered")
        void sendOtp_WhenPhoneNotRegistered_ShouldThrowException() {
            when(userRepository.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> smsLoginStrategy.sendOtp(sendOtpRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Bu telefon numarasına kayıtlı kullanıcı bulunamadı");

            verify(userRepository).findByPhoneNumber(PHONE_NUMBER);
            verify(otpService, never()).generateOtp(anyString());
            verify(smsService, never()).sendOtp(anyString(), anyString());
        }

        @Test
        @DisplayName("Should mask phone number in response")
        void sendOtp_ShouldMaskPhoneNumber() {
            when(userRepository.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(user));
            when(otpService.generateOtp(PHONE_NUMBER)).thenReturn(OTP_CODE);
            doNothing().when(smsService).sendOtp(anyString(), anyString());

            OtpResponse result = smsLoginStrategy.sendOtp(sendOtpRequest);

            assertThat(result.getPhoneNumber()).isNotEqualTo(PHONE_NUMBER);
            assertThat(result.getPhoneNumber()).contains("****");
        }

        @Test
        @DisplayName("Should return expiration time of 300 seconds (5 minutes)")
        void sendOtp_ShouldReturnCorrectExpirationTime() {
            when(userRepository.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(user));
            when(otpService.generateOtp(PHONE_NUMBER)).thenReturn(OTP_CODE);
            doNothing().when(smsService).sendOtp(anyString(), anyString());

            OtpResponse result = smsLoginStrategy.sendOtp(sendOtpRequest);

            assertThat(result.getExpiresInSeconds()).isEqualTo(300);
        }
    }

    @Nested
    @DisplayName("Verify OTP and Login Tests")
    class VerifyOtpAndLoginTests {

        @Test
        @DisplayName("Should login successfully with correct OTP")
        void verifyOtpAndLogin_WithCorrectOtp_ShouldReturnLoginResponse() {
            when(userRepository.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(user));
            when(otpService.verifyOtp(PHONE_NUMBER, OTP_CODE)).thenReturn(true);
            when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
            when(jwtService.getExpirationTime()).thenReturn(3600000L);

            LoginResponse result = smsLoginStrategy.verifyOtpAndLogin(verifyOtpRequest);

            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo("accessToken");
            assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
            assertThat(result.getTokenType()).isEqualTo("Bearer");
            assertThat(result.getUserId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("murat@test.com");
            assertThat(result.getFirstName()).isEqualTo("Murat");
            assertThat(result.getLastName()).isEqualTo("Test");
            assertThat(result.getRole()).isEqualTo("USER");
            assertThat(result.getStatus()).isEqualTo("SUCCESS");
            assertThat(result.getLoginTime()).isNotNull();

            verify(userRepository).findByPhoneNumber(PHONE_NUMBER);
            verify(otpService).verifyOtp(PHONE_NUMBER, OTP_CODE);
            verify(jwtService).generateToken(user);
            verify(jwtService).generateRefreshToken(user);
        }

        @Test
        @DisplayName("Should throw exception when phone number not registered")
        void verifyOtpAndLogin_WhenPhoneNotRegistered_ShouldThrowException() {
            when(userRepository.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> smsLoginStrategy.verifyOtpAndLogin(verifyOtpRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Bu telefon numarasına kayıtlı kullanıcı bulunamadı");

            verify(userRepository).findByPhoneNumber(PHONE_NUMBER);
            verify(otpService, never()).verifyOtp(anyString(), anyString());
            verify(jwtService, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when OTP verification fails")
        void verifyOtpAndLogin_WhenOtpInvalid_ShouldThrowException() {
            when(userRepository.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(user));
            when(otpService.verifyOtp(PHONE_NUMBER, OTP_CODE))
                    .thenThrow(new RuntimeException("OTP kodu yanlış"));

            assertThatThrownBy(() -> smsLoginStrategy.verifyOtpAndLogin(verifyOtpRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("OTP kodu yanlış");

            verify(jwtService, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("Should include phone number in login response")
        void verifyOtpAndLogin_ShouldIncludePhoneNumber() {
            when(userRepository.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(user));
            when(otpService.verifyOtp(PHONE_NUMBER, OTP_CODE)).thenReturn(true);
            when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
            when(jwtService.getExpirationTime()).thenReturn(3600000L);

            LoginResponse result = smsLoginStrategy.verifyOtpAndLogin(verifyOtpRequest);

            assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        }

        @Test
        @DisplayName("Should include expiration time in response")
        void verifyOtpAndLogin_ShouldIncludeExpirationTime() {
            when(userRepository.findByPhoneNumber(PHONE_NUMBER)).thenReturn(Optional.of(user));
            when(otpService.verifyOtp(PHONE_NUMBER, OTP_CODE)).thenReturn(true);
            when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
            when(jwtService.getExpirationTime()).thenReturn(3600000L);

            LoginResponse result = smsLoginStrategy.verifyOtpAndLogin(verifyOtpRequest);

            assertThat(result.getExpiresIn()).isEqualTo(3600000L);
        }
    }

    @Nested
    @DisplayName("Login Type Tests")
    class LoginTypeTests {

        @Test
        @DisplayName("Should return SMS as login type")
        void getLoginType_ShouldReturnSms() {
            String loginType = smsLoginStrategy.getLoginType();

            assertThat(loginType).isEqualTo("SMS");
        }
    }
}

