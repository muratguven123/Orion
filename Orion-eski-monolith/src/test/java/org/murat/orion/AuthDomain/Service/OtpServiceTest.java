package org.murat.orion.AuthDomain.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.murat.orion.AuthDomain.Entity.OtpCode;
import org.murat.orion.AuthDomain.Repository.OtpRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OtpService Unit Tests")
class OtpServiceTest {

    @Mock
    private OtpRepository otpRepository;

    @InjectMocks
    private OtpService otpService;

    private OtpCode validOtp;
    private OtpCode expiredOtp;
    private OtpCode usedOtp;
    private OtpCode maxAttemptsOtp;

    private static final String PHONE_NUMBER = "+905551234567";
    private static final String OTP_CODE = "123456";

    @BeforeEach
    void setUp() {
        validOtp = OtpCode.builder()
                .id(1L)
                .phoneNumber(PHONE_NUMBER)
                .code(OTP_CODE)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .attempts(0)
                .build();

        expiredOtp = OtpCode.builder()
                .id(2L)
                .phoneNumber(PHONE_NUMBER)
                .code(OTP_CODE)
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .expiresAt(LocalDateTime.now().minusMinutes(5))
                .isUsed(false)
                .attempts(0)
                .build();
    }

    @Nested
    @DisplayName("Generate OTP Tests")
    class GenerateOtpTests {

        @Test
        @DisplayName("Should generate 6 digit OTP code")
        void generateOtp_ShouldReturn6DigitCode() {
            doNothing().when(otpRepository).deleteAllByPhoneNumberAndIsUsedFalse(anyString());
            when(otpRepository.save(any(OtpCode.class))).thenAnswer(invocation -> invocation.getArgument(0));

            String result = otpService.generateOtp(PHONE_NUMBER);

            assertThat(result).isNotNull();
            assertThat(result).hasSize(6);
            assertThat(result).matches("\\d{6}");
        }

        @Test
        @DisplayName("Should delete previous unused OTPs before generating new one")
        void generateOtp_ShouldDeletePreviousUnusedOtps() {
            doNothing().when(otpRepository).deleteAllByPhoneNumberAndIsUsedFalse(PHONE_NUMBER);
            when(otpRepository.save(any(OtpCode.class))).thenAnswer(invocation -> invocation.getArgument(0));

            otpService.generateOtp(PHONE_NUMBER);

            verify(otpRepository).deleteAllByPhoneNumberAndIsUsedFalse(PHONE_NUMBER);
        }

        @Test
        @DisplayName("Should save new OTP to repository")
        void generateOtp_ShouldSaveOtpToRepository() {
            doNothing().when(otpRepository).deleteAllByPhoneNumberAndIsUsedFalse(anyString());
            when(otpRepository.save(any(OtpCode.class))).thenAnswer(invocation -> invocation.getArgument(0));

            otpService.generateOtp(PHONE_NUMBER);

            verify(otpRepository).save(any(OtpCode.class));
        }

        @Test
        @DisplayName("Should set expiration time to 5 minutes")
        void generateOtp_ShouldSetExpirationTo5Minutes() {
            doNothing().when(otpRepository).deleteAllByPhoneNumberAndIsUsedFalse(anyString());
            when(otpRepository.save(any(OtpCode.class))).thenAnswer(invocation -> {
                OtpCode otp = invocation.getArgument(0);
                assertThat(otp.getExpiresAt()).isAfter(LocalDateTime.now());
                assertThat(otp.getExpiresAt()).isBefore(LocalDateTime.now().plusMinutes(6));
                return otp;
            });

            otpService.generateOtp(PHONE_NUMBER);

            verify(otpRepository).save(any(OtpCode.class));
        }

        @Test
        @DisplayName("Should set initial attempts to 0")
        void generateOtp_ShouldSetInitialAttemptsToZero() {
            doNothing().when(otpRepository).deleteAllByPhoneNumberAndIsUsedFalse(anyString());
            when(otpRepository.save(any(OtpCode.class))).thenAnswer(invocation -> {
                OtpCode otp = invocation.getArgument(0);
                assertThat(otp.getAttempts()).isEqualTo(0);
                return otp;
            });

            otpService.generateOtp(PHONE_NUMBER);

            verify(otpRepository).save(any(OtpCode.class));
        }

        @Test
        @DisplayName("Should set isUsed to false")
        void generateOtp_ShouldSetIsUsedToFalse() {
            doNothing().when(otpRepository).deleteAllByPhoneNumberAndIsUsedFalse(anyString());
            when(otpRepository.save(any(OtpCode.class))).thenAnswer(invocation -> {
                OtpCode otp = invocation.getArgument(0);
                assertThat(otp.getIsUsed()).isFalse();
                return otp;
            });

            otpService.generateOtp(PHONE_NUMBER);

            verify(otpRepository).save(any(OtpCode.class));
        }
    }

    @Nested
    @DisplayName("Verify OTP Tests")
    class VerifyOtpTests {

        @Test
        @DisplayName("Should verify OTP successfully with correct code")
        void verifyOtp_WithCorrectCode_ShouldReturnTrue() {
            when(otpRepository.findTopByPhoneNumberAndIsUsedFalseOrderByCreatedAtDesc(PHONE_NUMBER))
                    .thenReturn(Optional.of(validOtp));
            when(otpRepository.save(any(OtpCode.class))).thenReturn(validOtp);

            boolean result = otpService.verifyOtp(PHONE_NUMBER, OTP_CODE);

            assertThat(result).isTrue();
            verify(otpRepository, times(2)).save(validOtp);
        }

        @Test
        @DisplayName("Should throw exception when OTP not found")
        void verifyOtp_WhenOtpNotFound_ShouldThrowException() {
            when(otpRepository.findTopByPhoneNumberAndIsUsedFalseOrderByCreatedAtDesc(PHONE_NUMBER))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> otpService.verifyOtp(PHONE_NUMBER, OTP_CODE))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("OTP bulunamadı");

            verify(otpRepository, never()).save(any(OtpCode.class));
        }

        @Test
        @DisplayName("Should throw exception when OTP is expired")
        void verifyOtp_WhenOtpExpired_ShouldThrowException() {
            when(otpRepository.findTopByPhoneNumberAndIsUsedFalseOrderByCreatedAtDesc(PHONE_NUMBER))
                    .thenReturn(Optional.of(expiredOtp));
            when(otpRepository.save(any(OtpCode.class))).thenReturn(expiredOtp);

            assertThatThrownBy(() -> otpService.verifyOtp(PHONE_NUMBER, OTP_CODE))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("OTP geçersiz veya süresi dolmuş");
        }

        @Test
        @DisplayName("Should throw exception when OTP code is wrong")
        void verifyOtp_WhenCodeIsWrong_ShouldThrowException() {
            when(otpRepository.findTopByPhoneNumberAndIsUsedFalseOrderByCreatedAtDesc(PHONE_NUMBER))
                    .thenReturn(Optional.of(validOtp));
            when(otpRepository.save(any(OtpCode.class))).thenReturn(validOtp);

            assertThatThrownBy(() -> otpService.verifyOtp(PHONE_NUMBER, "000000"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("OTP kodu yanlış");
        }

        @Test
        @DisplayName("Should increment attempts on wrong code")
        void verifyOtp_WhenCodeIsWrong_ShouldIncrementAttempts() {
            OtpCode otpWithZeroAttempts = OtpCode.builder()
                    .id(1L)
                    .phoneNumber(PHONE_NUMBER)
                    .code(OTP_CODE)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(5))
                    .isUsed(false)
                    .attempts(0)
                    .build();

            when(otpRepository.findTopByPhoneNumberAndIsUsedFalseOrderByCreatedAtDesc(PHONE_NUMBER))
                    .thenReturn(Optional.of(otpWithZeroAttempts));
            when(otpRepository.save(any(OtpCode.class))).thenReturn(otpWithZeroAttempts);

            assertThatThrownBy(() -> otpService.verifyOtp(PHONE_NUMBER, "000000"))
                    .isInstanceOf(RuntimeException.class);

            assertThat(otpWithZeroAttempts.getAttempts()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should throw exception when max attempts reached")
        void verifyOtp_WhenMaxAttemptsReached_ShouldThrowException() {
            OtpCode otpWithTwoAttempts = OtpCode.builder()
                    .id(1L)
                    .phoneNumber(PHONE_NUMBER)
                    .code(OTP_CODE)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusMinutes(5))
                    .isUsed(false)
                    .attempts(2)
                    .build();

            when(otpRepository.findTopByPhoneNumberAndIsUsedFalseOrderByCreatedAtDesc(PHONE_NUMBER))
                    .thenReturn(Optional.of(otpWithTwoAttempts));
            when(otpRepository.save(any(OtpCode.class))).thenReturn(otpWithTwoAttempts);

            assertThatThrownBy(() -> otpService.verifyOtp(PHONE_NUMBER, "000000"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Çok fazla hatalı deneme. Lütfen yeni OTP isteyin.");
        }

        @Test
        @DisplayName("Should mark OTP as used after successful verification")
        void verifyOtp_WhenSuccessful_ShouldMarkOtpAsUsed() {
            when(otpRepository.findTopByPhoneNumberAndIsUsedFalseOrderByCreatedAtDesc(PHONE_NUMBER))
                    .thenReturn(Optional.of(validOtp));
            when(otpRepository.save(any(OtpCode.class))).thenReturn(validOtp);

            otpService.verifyOtp(PHONE_NUMBER, OTP_CODE);

            assertThat(validOtp.getIsUsed()).isTrue();
        }
    }
}

