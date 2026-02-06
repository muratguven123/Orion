package org.murat.orion.AuthDomain.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.AuthDomain.Config.JwtService;
import org.murat.orion.AuthDomain.Dto.Request.SendOtpRequest;
import org.murat.orion.AuthDomain.Dto.Request.VerifyOtpRequest;
import org.murat.orion.AuthDomain.Dto.Response.LoginResponse;
import org.murat.orion.AuthDomain.Dto.Response.OtpResponse;
import org.murat.orion.AuthDomain.Entity.User;
import org.murat.orion.AuthDomain.Repository.UserRepository;
import org.murat.orion.Notification.Events.Auth.LoginFailedEvent;
import org.murat.orion.Notification.Events.Auth.OtpSentEvent;
import org.murat.orion.Notification.Events.Auth.OtpVerifiedEvent;
import org.murat.orion.Notification.Events.Auth.SmsLoginEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class SmsLoginStrategy {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final SmsService smsService;
    private final JwtService jwtService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private static final int OTP_EXPIRY_SECONDS = 300;


    public OtpResponse sendOtp(SendOtpRequest request) {
        String phoneNumber = request.getPhoneNumber();

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> {
                    LoginFailedEvent failedEvent = LoginFailedEvent.builder()
                            .phoneNumber(phoneNumber)
                            .reason("Bu telefon numarasına kayıtlı kullanıcı bulunamadı")
                            .failedAt(LocalDateTime.now())
                            .build();
                    applicationEventPublisher.publishEvent(failedEvent);
                    return new RuntimeException("Bu telefon numarasına kayıtlı kullanıcı bulunamadı");
                });

        String otpCode = otpService.generateOtp(phoneNumber);

        smsService.sendOtp(phoneNumber, otpCode);

        log.info("OTP gönderildi - Telefon: {}", maskPhoneNumber(phoneNumber));

        OtpSentEvent otpEvent = OtpSentEvent.builder()
                .userId(user.getId())
                .phoneNumber(phoneNumber)
                .otpType("SMS_LOGIN")
                .sentAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(OTP_EXPIRY_SECONDS))
                .build();
        applicationEventPublisher.publishEvent(otpEvent);

        return OtpResponse.builder()
                .status("OTP_SENT")
                .message("Doğrulama kodu telefonunuza gönderildi")
                .phoneNumber(maskPhoneNumber(phoneNumber))
                .expiresInSeconds(OTP_EXPIRY_SECONDS)
                .timestamp(LocalDateTime.now())
                .build();
    }


    public LoginResponse verifyOtpAndLogin(VerifyOtpRequest request) {
        String phoneNumber = request.getPhoneNumber();
        String verificationCode = request.getVerificationCode();

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> {
                    LoginFailedEvent failedEvent = LoginFailedEvent.builder()
                            .phoneNumber(phoneNumber)
                            .reason("Bu telefon numarasına kayıtlı kullanıcı bulunamadı")
                            .failedAt(LocalDateTime.now())
                            .build();
                    applicationEventPublisher.publishEvent(failedEvent);
                    return new RuntimeException("Bu telefon numarasına kayıtlı kullanıcı bulunamadı");
                });

        try {
            otpService.verifyOtp(phoneNumber, verificationCode);
        } catch (RuntimeException e) {
            LoginFailedEvent failedEvent = LoginFailedEvent.builder()
                    .phoneNumber(phoneNumber)
                    .reason(e.getMessage())
                    .failedAt(LocalDateTime.now())
                    .build();
            applicationEventPublisher.publishEvent(failedEvent);
            throw e;
        }

        OtpVerifiedEvent otpVerifiedEvent = OtpVerifiedEvent.builder()
                .userId(user.getId())
                .phoneNumber(phoneNumber)
                .otpType("SMS_LOGIN")
                .verifiedAt(LocalDateTime.now())
                .build();
        applicationEventPublisher.publishEvent(otpVerifiedEvent);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("SMS login başarılı - Kullanıcı: {}", user.getEmail());

        SmsLoginEvent smsLoginEvent = SmsLoginEvent.builder()
                .userId(user.getId())
                .phoneNumber(phoneNumber)
                .loginAt(LocalDateTime.now())
                .build();
        applicationEventPublisher.publishEvent(smsLoginEvent);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .phoneNumber(user.getPhoneNumber())
                .loginTime(LocalDateTime.now())
                .status("SUCCESS")
                .build();
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "***";
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 2);
    }

    public String getLoginType() {
        return "SMS";
    }
}

