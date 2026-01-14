package org.murat.orion.AuthDomain.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SMS gönderme servisi
 * Gerçek implementasyonda SMS sağlayıcı (Twilio, Netgsm vb.) entegre edilecek
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    /**
     * SMS ile OTP gönderir
     *
     * @param phoneNumber Hedef telefon numarası
     * @param otpCode Gönderilecek OTP kodu
     */
    public void sendOtp(String phoneNumber, String otpCode) {
        // TODO: Gerçek SMS sağlayıcı entegrasyonu yapılacak (Twilio, Netgsm, vb.)

        String message = String.format("Orion doğrulama kodunuz: %s. Bu kod 5 dakika geçerlidir.", otpCode);

        // Şimdilik sadece log'a yazıyoruz
        log.info("SMS Gönderiliyor - Telefon: {}, Mesaj: {}", maskPhoneNumber(phoneNumber), message);

        // Gerçek implementasyon örneği:
        // twilioClient.sendSms(phoneNumber, message);
        // netgsmClient.sendSms(phoneNumber, message);

        log.info("SMS başarıyla gönderildi - Telefon: {}", maskPhoneNumber(phoneNumber));
    }

    /**
     * Telefon numarasını loglar için maskeler
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "***";
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 2);
    }
}

