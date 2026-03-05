package com.org.orion.notification_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.org.orion.notification_service.dto.AccountCreditedEvent;
import com.org.orion.notification_service.dto.AccountDebitedEvent;
import com.org.orion.notification_service.dto.InvestmentBuyEvent;
import com.org.orion.notification_service.dto.InvestmentSellEvent;
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

            } else if (routingKey.endsWith("account.credited") || (typeId != null && typeId.contains("AccountCreditedEvent"))) {

                AccountCreditedEvent event = objectMapper.readValue(jsonPayload, AccountCreditedEvent.class);
                handleAccountCreditedEvent(event);

            } else if (routingKey.endsWith("invest.buy") || (typeId != null && typeId.contains("InvestmentBuyEvent"))) {

                InvestmentBuyEvent event = objectMapper.readValue(jsonPayload, InvestmentBuyEvent.class);
                handleInvestmentBuyEvent(event);

            } else if (routingKey.endsWith("invest.sell") || (typeId != null && typeId.contains("InvestmentSellEvent"))) {

                InvestmentSellEvent event = objectMapper.readValue(jsonPayload, InvestmentSellEvent.class);
                handleInvestmentSellEvent(event);

            } else {
                log.warn("Bu Routing Key için bir işleyici (handler) tanımlanmamış: {}", routingKey);
            }

        } catch (Exception e) {
            log.error("Mesaj JSON'dan DTO'ya dönüştürülemedi. Hata: {}", e.getMessage(), e);
        }
    }
    private void handleAccountDebitedEvent(AccountDebitedEvent event) {
        log.info("HESAP BORÇLANDIRMA: UserID: {}, Tutar: {}, Mesaj: {}",
                event.getUserId(), event.getAmount(), event.getMessage());
        log.info("Hesap borçlandırma bildirimi gönderiliyor... [BAŞARILI]");
    }

    private void handleAccountCreditedEvent(AccountCreditedEvent event) {
        log.info("HESAP ALACAKLANDIRMA: UserID: {}, Tutar: {}, Mesaj: {}",
                event.getUserId(), event.getAmount(), event.getMessage());
        log.info("Hesap alacaklandırma bildirimi gönderiliyor... [BAŞARILI]");
    }

    private void handleInvestmentBuyEvent(InvestmentBuyEvent event) {
        log.info("YATIRIM ALIM: UserID: {}, Sembol: {}, Tip: {}, Adet: {}, Fiyat: {}, Toplam: {}",
                event.getUserId(), event.getSymbol(), event.getType(),
                event.getQuantity(), event.getPrice(), event.getTotalCost());
        log.info("Yatırım alım bildirimi gönderiliyor: {} {} adet @ {} [BAŞARILI]",
                event.getSymbol(), event.getQuantity(), event.getPrice());
    }

    private void handleInvestmentSellEvent(InvestmentSellEvent event) {
        log.info("YATIRIM SATIM: UserID: {}, Sembol: {}, Tip: {}, Adet: {}, Fiyat: {}, Toplam Gelir: {}",
                event.getUserId(), event.getSymbol(), event.getType(),
                event.getQuantity(), event.getPrice(), event.getTotalProceeds());
        log.info("Yatırım satım bildirimi gönderiliyor: {} {} adet @ {} [BAŞARILI]",
                event.getSymbol(), event.getQuantity(), event.getPrice());
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