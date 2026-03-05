package com.org.orion.notification_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.org.orion.notification_service.dto.AccountDebitedEvent;
import com.org.orion.notification_service.dto.OtpSentEvent;
import com.org.orion.notification_service.dto.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class NotificationConsumer {

    private final ObjectMapper objectMapper;

    public NotificationConsumer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @RabbitListener(queues = "notification.queue")
    public void consumeNotificationQueue(Message message) {

        String typeId = (String) message.getMessageProperties().getHeaders().get("__TypeId__");
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();


        String jsonPayload = new String(message.getBody(), StandardCharsets.UTF_8);


        log.info("RabbitMQ'dan mesaj alındı. RoutingKey: {}, TypeId: {}, Payload: {}", routingKey, typeId, jsonPayload);

        try {
            if (routingKey.endsWith("auth.registered") || (typeId != null && typeId.contains("UserRegisteredEvent"))) {

                UserRegisteredEvent event = objectMapper.readValue(jsonPayload, UserRegisteredEvent.class);
                handleUserRegisteredEvent(event);

            } else if (routingKey.endsWith("auth.otp") || (typeId != null && typeId.contains("OtpSentEvent"))) {

                OtpSentEvent event = objectMapper.readValue(jsonPayload, OtpSentEvent.class);
                handleOtpSentEvent(event);

            } else if (routingKey.endsWith("account.debited") || (typeId != null && typeId.contains("AccountDebitedEvent"))) {

                AccountDebitedEvent event = objectMapper.readValue(jsonPayload, AccountDebitedEvent.class);
                handleAccountDebitedEvent(event);

            } else {
                log.warn("Bu Routing Key için bir işleyici (handler) tanımlanmamış: {}", routingKey);
            }

        } catch (Exception e) {
            log.error("Mesaj JSON'dan DTO'ya dönüştürülemedi. Hata: {}", e.getMessage(), e);
        }
    }
    private void handleAccountDebitedEvent(AccountDebitedEvent event) {
        log.info("MESAJ ALINDI: UserID: {}, Tutar: {}, Mesaj: {}",
                event.getUserId(), event.getAmount(), event.getMessage());
        log.info("SMS Gönderiliyor... [BAŞARILI]");
    }

    private void handleUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Yeni kullanıcı kaydı alındı - UserID: {}, Email: {}, Ad: {} {}",
                event.getUserId(), event.getEmail(), event.getFirstName(), event.getLastName());
        log.info("Hoş geldiniz e-postası gönderiliyor: {} [BAŞARILI]", event.getEmail());
    }

    private void handleOtpSentEvent(OtpSentEvent event) {
        log.info("OTP bildirimi alındı - UserID: {}, Telefon: {}, Tip: {}",
                event.getUserId(), event.getPhoneNumber(), event.getOtpType());
        log.info("OTP SMS bildirimi gönderiliyor: {} [BAŞARILI]", event.getPhoneNumber());
    }
}