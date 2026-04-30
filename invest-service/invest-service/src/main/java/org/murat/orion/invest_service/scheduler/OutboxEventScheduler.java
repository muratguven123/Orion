package org.murat.orion.invest_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.invest_service.config.RabbitMqConfig;
import org.murat.orion.invest_service.entity.OutboxEvent;
import org.murat.orion.invest_service.repository.OutboxEventRepository;
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
            "InvestmentBuyEvent", RabbitMqConfig.ROUTING_KEY_INVEST_BUY,
            "InvestmentSellEvent", RabbitMqConfig.ROUTING_KEY_INVEST_SELL
    );

    private static final Map<String, String> TYPE_ID_MAP = Map.of(
            "InvestmentBuyEvent", "org.murat.orion.invest_service.event.InvestmentBuyEvent",
            "InvestmentSellEvent", "org.murat.orion.invest_service.event.InvestmentSellEvent"
    );

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Invest Outbox scheduler: {} adet bekleyen event bulundu", pendingEvents.size());

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

                log.info("Invest outbox event published: id={}, type={}, routingKey={}",
                        event.getId(), event.getEventType(), routingKey);

            } catch (Exception e) {
                log.error("Invest outbox event gönderilemedi: id={}, type={}, hata={}",
                        event.getId(), event.getEventType(), e.getMessage(), e);
            }
        }
    }
}

