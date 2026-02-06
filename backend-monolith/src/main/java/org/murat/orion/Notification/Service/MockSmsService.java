package org.murat.orion.Notification.Service;

import lombok.extern.slf4j.Slf4j;
import org.murat.orion.Notification.İnterface.smsProvider;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MockSmsService implements smsProvider {
    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info(" [MOCK SMS] Gerçek SMS gönderimi simüle ediliyor...");
        log.info(" Alıcı: {} | Mesaj: {}", phoneNumber, message);
    }
    public boolean supports(String provider) {
        return "MOCK".equalsIgnoreCase(provider);
    }
}
