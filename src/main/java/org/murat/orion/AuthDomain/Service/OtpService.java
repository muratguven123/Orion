package org.murat.orion.AuthDomain.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.AuthDomain.Entity.OtpCode;
import org.murat.orion.AuthDomain.Repository.OtpRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpRepository otpRepository;

    private static final int OTP_EXPIRY_MINUTES = 5;

    private static final int OTP_LENGTH = 6;


    @Transactional
    public String generateOtp(String phoneNumber) {
        otpRepository.deleteAllByPhoneNumberAndIsUsedFalse(phoneNumber);

        String otpCode = generateRandomOtp();

        OtpCode otp = OtpCode.builder()
                .phoneNumber(phoneNumber)
                .code(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .isUsed(false)
                .attempts(0)
                .build();

        otpRepository.save(otp);

        log.info("OTP oluşturuldu - Telefon: {}", maskPhoneNumber(phoneNumber));

        return otpCode;
    }


    @Transactional
    public boolean verifyOtp(String phoneNumber, String code) {
        OtpCode otp = otpRepository.findTopByPhoneNumberAndIsUsedFalseOrderByCreatedAtDesc(phoneNumber)
                .orElseThrow(() -> new RuntimeException("OTP bulunamadı"));

        otp.setAttempts(otp.getAttempts() + 1);
        otpRepository.save(otp);

        if (!otp.isValid()) {
            log.warn("OTP geçersiz veya süresi dolmuş - Telefon: {}", maskPhoneNumber(phoneNumber));
            throw new RuntimeException("OTP geçersiz veya süresi dolmuş");
        }

        if (!otp.getCode().equals(code)) {
            log.warn("Yanlış OTP girişi - Telefon: {}, Deneme: {}", maskPhoneNumber(phoneNumber), otp.getAttempts());

            if (otp.getAttempts() >= 3) {
                throw new RuntimeException("Çok fazla hatalı deneme. Lütfen yeni OTP isteyin.");
            }

            throw new RuntimeException("OTP kodu yanlış");
        }

        otp.setIsUsed(true);
        otpRepository.save(otp);

        log.info("OTP doğrulandı - Telefon: {}", maskPhoneNumber(phoneNumber));

        return true;
    }


    private String generateRandomOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }


    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "***";
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 2);
    }
}

