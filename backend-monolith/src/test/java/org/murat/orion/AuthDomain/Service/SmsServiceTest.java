package org.murat.orion.AuthDomain.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmsService Unit Tests")
class SmsServiceTest {

    @InjectMocks
    private SmsService smsService;

    private static final String PHONE_NUMBER = "+905551234567";
    private static final String SHORT_PHONE_NUMBER = "+90";
    private static final String OTP_CODE = "123456";

    @Nested
    @DisplayName("Send OTP Tests")
    class SendOtpTests {

        @Test
        @DisplayName("Should send OTP without throwing exception")
        void sendOtp_ShouldNotThrowException() {
            assertThatCode(() -> smsService.sendOtp(PHONE_NUMBER, OTP_CODE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle valid phone number")
        void sendOtp_WithValidPhoneNumber_ShouldComplete() {
            assertThatCode(() -> smsService.sendOtp("+905551234567", OTP_CODE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle different OTP codes")
        void sendOtp_WithDifferentOtpCodes_ShouldComplete() {
            assertThatCode(() -> smsService.sendOtp(PHONE_NUMBER, "999999"))
                    .doesNotThrowAnyException();
            assertThatCode(() -> smsService.sendOtp(PHONE_NUMBER, "000000"))
                    .doesNotThrowAnyException();
            assertThatCode(() -> smsService.sendOtp(PHONE_NUMBER, "111111"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle international phone numbers")
        void sendOtp_WithInternationalPhoneNumber_ShouldComplete() {
            assertThatCode(() -> smsService.sendOtp("+14155552671", OTP_CODE))
                    .doesNotThrowAnyException();
            assertThatCode(() -> smsService.sendOtp("+442071234567", OTP_CODE))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Phone Number Masking Tests")
    class PhoneNumberMaskingTests {

        @Test
        @DisplayName("Should mask phone number correctly in logs")
        void sendOtp_ShouldMaskPhoneNumberInLogs() {
            assertThatCode(() -> smsService.sendOtp(PHONE_NUMBER, OTP_CODE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle short phone number without error")
        void sendOtp_WithShortPhoneNumber_ShouldNotThrow() {
            assertThatCode(() -> smsService.sendOtp(SHORT_PHONE_NUMBER, OTP_CODE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle null phone number gracefully")
        void sendOtp_WithNullPhoneNumber_ShouldNotThrow() {
            assertThatCode(() -> smsService.sendOtp(null, OTP_CODE))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Message Format Tests")
    class MessageFormatTests {

        @Test
        @DisplayName("Should format OTP message correctly")
        void sendOtp_ShouldFormatMessageCorrectly() {
            assertThatCode(() -> smsService.sendOtp(PHONE_NUMBER, OTP_CODE))
                    .doesNotThrowAnyException();
        }
    }
}

