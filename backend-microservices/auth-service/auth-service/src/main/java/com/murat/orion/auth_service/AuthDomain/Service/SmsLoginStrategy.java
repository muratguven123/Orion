package com.murat.orion.auth_service.AuthDomain.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.murat.orion.auth_service.AuthDomain.Config.JwtService;
import com.murat.orion.auth_service.AuthDomain.Dto.Request.SendOtpRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Request.VerifyOtpRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Response.LoginResponse;
import com.murat.orion.auth_service.AuthDomain.Dto.Response.OtpResponse;
import com.murat.orion.auth_service.AuthDomain.Entity.OutboxEvent;
import com.murat.orion.auth_service.AuthDomain.Entity.User;
import com.murat.orion.auth_service.AuthDomain.Events.OtpSentEvent;
import com.murat.orion.auth_service.AuthDomain.Repository.OutboxEventRepository;
import com.murat.orion.auth_service.AuthDomain.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class SmsLoginStrategy {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final SmsService smsService;
    private final JwtService jwtService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    private static final int OTP_EXPIRY_SECONDS = 300;


    @Transactional
    public OtpResponse sendOtp(SendOtpRequest request) {
        String phoneNumber = request.getPhoneNumber();

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Bu telefon numarasına kayıtlı kullanıcı bulunamadı"));

        String otpCode = otpService.generateOtp(phoneNumber);

        smsService.sendOtp(phoneNumber, otpCode);

        LocalDateTime now = LocalDateTime.now();
        OtpSentEvent event = new OtpSentEvent(
                user.getId(),
                phoneNumber,
                user.getEmail(),
                otpCode,
                "SMS",
                now,
                now.plusSeconds(OTP_EXPIRY_SECONDS)
        );

        try {
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("User");
            outboxEvent.setAggregateId(user.getId());
            outboxEvent.setEventType("OtpSentEvent");
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            outboxEvent.setProcessed(false);
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Event JSON serialize hatası", e);
        }

        log.info("OTP sent outbox event saved - Telefon: {}", maskPhoneNumber(phoneNumber));

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

