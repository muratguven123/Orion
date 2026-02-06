package org.murat.orion.AuthDomain.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {



    public void sendOtp(String phoneNumber, String otpCode) {


        String message = String.format("Orion doğrulama kodunuz: %s. Bu kod 5 dakika geçerlidir.", otpCode);

        log.info("SMS Gönderiliyor - Telefon: {}, Mesaj: {}", maskPhoneNumber(phoneNumber), message);



        log.info("SMS başarıyla gönderildi - Telefon: {}", maskPhoneNumber(phoneNumber));
    }


    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "***";
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 2);
    }
}

