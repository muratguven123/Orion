package org.murat.orion.AuthDomain.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.AuthDomain.Dto.Request.LoginRequest;
import org.murat.orion.AuthDomain.Dto.Response.LoginResponse;
import org.murat.orion.AuthDomain.Loginİnterface;
import org.murat.orion.AuthDomain.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class SmsLoginStrategy implements Loginİnterface {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final SmsService smsService;
    private final JwtService jwtService;

    /**
     * SMS ile login işlemi
     *
     * İki aşamalı çalışır:
     * 1. Aşama: verificationCode boşsa -> OTP oluştur ve SMS gönder
     * 2. Aşama: verificationCode doluysa -> OTP doğrula ve token üret
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String phoneNumber = loginRequest.getPhoneNumber();
        String verificationCode = loginRequest.getVerificationCode();

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new RuntimeException("Telefon numarası gereklidir");
        }

        var user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Bu telefon numarasına kayıtlı kullanıcı bulunamadı"));

        if (verificationCode == null || verificationCode.isBlank()) {
            return sendOtpAndReturnPendingResponse(phoneNumber);
        }

        return verifyOtpAndLogin(user, verificationCode);
    }

    /**
     * OTP oluştur, SMS gönder ve bekliyor yanıtı dön
     */
    private LoginResponse sendOtpAndReturnPendingResponse(String phoneNumber) {
        String otpCode = otpService.generateOtp(phoneNumber);

        // SMS gönder
        smsService.sendOtp(phoneNumber, otpCode);

        log.info("OTP gönderildi, doğrulama bekleniyor - Telefon: {}", maskPhoneNumber(phoneNumber));

        return LoginResponse.builder()
                .status("OTP_SENT")
                .phoneNumber(phoneNumber)
                .loginTime(LocalDateTime.now())
                .build();
    }

    /**
     * OTP doğrula ve login işlemini tamamla
     */
    private LoginResponse verifyOtpAndLogin(org.murat.orion.AuthDomain.Entity.User user, String verificationCode) {
        // OTP doğrula
        otpService.verifyOtp(user.getPhoneNumber(), verificationCode);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("SMS login başarılı - Kullanıcı: {}", user.getEmail());

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

    /**
     * Telefon numarasını maskeler
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "***";
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 2);
    }

    @Override
    public String getLoginType() {
        return "SMS";
    }
}

