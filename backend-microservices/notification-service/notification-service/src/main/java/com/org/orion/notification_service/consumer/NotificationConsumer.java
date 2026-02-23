package com.org.orion.notification_service.consumer;

import com.org.orion.notification_service.dto.AccountDebitedEvent;
import com.org.orion.notification_service.dto.OtpSentEvent;
import com.org.orion.notification_service.dto.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RabbitListener(queues = "notification.queue")
public class NotificationConsumer {

    @RabbitHandler
    public void handleAccountDebitedEvent(AccountDebitedEvent event) {
        log.info("MESAJ ALINDI: UserID: {}, Tutar: {}, Mesaj: {}",
                event.getUserId(), event.getAmount(), event.getMessage());
        log.info("SMS Gönderiliyor... [BAŞARILI]");
    }

    @RabbitHandler
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Yeni kullanıcı kaydı alındı - UserID: {}, Email: {}, Ad: {} {}",
                event.getUserId(), event.getEmail(), event.getFirstName(), event.getLastName());
        log.info("Hoş geldiniz e-postası gönderiliyor: {} [BAŞARILI]", event.getEmail());
    }

    @RabbitHandler
    public void handleOtpSentEvent(OtpSentEvent event) {
        log.info("OTP bildirimi alındı - UserID: {}, Telefon: {}, Tip: {}",
                event.getUserId(), event.getPhoneNumber(), event.getOtpType());
        log.info("OTP SMS bildirimi gönderiliyor: {} [BAŞARILI]", event.getPhoneNumber());
    }

    @RabbitHandler(isDefault = true)
    public void handleDefault(Object event) {
        log.warn("Bilinmeyen mesaj tipi alındı: {}", event);
    }
}
