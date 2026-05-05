package org.murat.orion.payment_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.payment_service.config.KafkaConfig;
import org.murat.orion.payment_service.config.RabbitMqConfig;
import org.murat.orion.payment_service.entity.OutboxEvent;
import org.murat.orion.payment_service.repository.OutboxEventRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, String> kafkaTemplate;


    private static final Map<String, String> ROUTING_KEY_MAP = Map.of(
            "PaymentDepositEvent", RabbitMqConfig.ROUTING_KEY_PAYMENT_DEPOSIT,
            "PaymentWithdrawEvent", RabbitMqConfig.ROUTING_KEY_PAYMENT_WITHDRAW,
            "PaymentTransferEvent", RabbitMqConfig.ROUTING_KEY_PAYMENT_TRANSFER
    );

    private static final Map<String, String> TYPE_ID_MAP = Map.of(
            "PaymentDepositEvent", "org.murat.orion.payment_service.event.PaymentDepositEvent",
            "PaymentWithdrawEvent", "org.murat.orion.payment_service.event.PaymentWithdrawEvent",
            "PaymentTransferEvent", "org.murat.orion.payment_service.event.PaymentTransferEvent"
    );

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Payment Outbox scheduler: {} adet bekleyen event bulundu", pendingEvents.size());

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
                publishToKafka(event);
                outboxEventRepository.save(event);

                log.info("Payment outbox event published: id={}, type={}, routingKey={}",
                        event.getId(), event.getEventType(), routingKey);

            } catch (Exception e) {
                log.error("Payment outbox event gönderilemedi: id={}, type={}, hata={}",
                        event.getId(), event.getEventType(), e.getMessage(), e);
            }
        }
    }

    private void publishToKafka(OutboxEvent event) {
        String key = event.getAggregateType() + "-" + event.getAggregateId();
        kafkaTemplate.send(KafkaConfig.TOPIC_PAYMENT_EVENTS, key, event.getPayload())
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka'ya gönderilemedi: id={}, hata={}", event.getId(), ex.getMessage());
                    } else {
                        log.debug("Kafka'ya gönderildi: id={}, partition={}, offset={}",
                                event.getId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
