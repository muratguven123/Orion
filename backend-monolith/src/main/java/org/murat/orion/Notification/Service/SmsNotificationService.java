package org.murat.orion.Notification.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.Notification.İnterface.smsProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class SmsNotificationService {
    private final List<smsProvider> strategies;
    @Value("${sms.provider.active:MOCK}")
    private String activeProvider;
    @Async
    public void sendSms(Long userId, String phoneNumber, String message) {
        for (smsProvider strategy : strategies) {
            if (strategy.supports(activeProvider)) {
                strategy.sendSms(phoneNumber, message);
                log.info("SMS gönderildi: Kullanıcı ID: {}, Telefon Numarası: {}, Mesaj: {}", userId, phoneNumber, message);
                return;
            }
        }
        log.error("Geçerli SMS sağlayıcısı bulunamadı: {}", activeProvider);
    }
}

