package com.murat.orion.auth_service.AuthDomain.Scheduler;

import com.murat.orion.auth_service.AuthDomain.Config.RabbitMqConfig;
import com.murat.orion.auth_service.AuthDomain.Entity.OutboxEvent;
import com.murat.orion.auth_service.AuthDomain.Repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    private static final Map<String, String> ROUTING_KEY_MAP = Map.of(
            "UserRegisteredEvent", RabbitMqConfig.ROUTING_KEY_USER_REGISTERED,
            "OtpSentEvent", RabbitMqConfig.ROUTING_KEY_OTP_SENT,
            "EmailLoginEvent", RabbitMqConfig.ROUTING_KEY_EMAIL_LOGIN,
            "SmsLoginEvent", RabbitMqConfig.ROUTING_KEY_SMS_LOGIN
    );
    private static final Map<String, String> TYPE_ID_MAP = Map.of(
            "UserRegisteredEvent", "com.murat.orion.auth_service.AuthDomain.Events.UserRegisteredEvent",
            "OtpSentEvent", "com.murat.orion.auth_service.AuthDomain.Events.OtpSentEvent",
            "EmailLoginEvent", "com.murat.orion.auth_service.AuthDomain.Events.EmailLoginEvent",
            "SmsLoginEvent", "com.murat.orion.auth_service.AuthDomain.Events.SmsLoginEvent"
    );



    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Outbox scheduler: {} adet bekleyen event bulundu", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                String routingKey = ROUTING_KEY_MAP.getOrDefault(
                        event.getEventType(),
                        "unknown.event"
                );
                String typeId = TYPE_ID_MAP.getOrDefault(
                        event.getEventType(),
                        "java.lang.Object"
                );
                MessageProperties messageProperties = new MessageProperties();
                messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                messageProperties.setHeader("__TypeId__", typeId);

                Message message = new Message(event.getPayload().getBytes("UTF-8"), messageProperties);

                rabbitTemplate.convertAndSend(
                        RabbitMqConfig.INTERNAL_EXCHANGE,
                        routingKey,
                        message
                );

                event.setProcessed(true);
                outboxEventRepository.save(event);

                log.info("Outbox event published: id={}, type={}, routingKey={}",
                        event.getId(), event.getEventType(), routingKey);

            } catch (Exception e) {
                log.error("Outbox event gönderilemedi: id={}, type={}, hata={}",
                        event.getId(), event.getEventType(), e.getMessage(), e);
            }
        }
    }
}

