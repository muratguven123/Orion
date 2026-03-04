package com.murat.orion.auth_service.AuthDomain.Scheduler;

import com.murat.orion.auth_service.AuthDomain.Config.RabbitMqConfig;
import com.murat.orion.auth_service.AuthDomain.Entity.OutboxEvent;
import com.murat.orion.auth_service.AuthDomain.Repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            "OtpSentEvent", RabbitMqConfig.ROUTING_KEY_OTP_SENT
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

                rabbitTemplate.convertAndSend(
                        RabbitMqConfig.INTERNAL_EXCHANGE,
                        routingKey,
                        event.getPayload()
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

