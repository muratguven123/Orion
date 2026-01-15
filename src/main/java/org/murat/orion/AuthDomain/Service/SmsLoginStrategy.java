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

    private static final int OTP_EXPIRY_SECONDS = 300;


    public OtpResponse sendOtp(SendOtpRequest request) {
        String phoneNumber = request.getPhoneNumber();

        userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Bu telefon numarasına kayıtlı kullanıcı bulunamadı"));

        String otpCode = otpService.generateOtp(phoneNumber);

        smsService.sendOtp(phoneNumber, otpCode);

        log.info("OTP gönderildi - Telefon: {}", maskPhoneNumber(phoneNumber));

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
                .orElseThrow(() -> new RuntimeException("Bu telefon numarasına kayıtlı kullanıcı bulunamadı"));

        otpService.verifyOtp(phoneNumber, verificationCode);

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

