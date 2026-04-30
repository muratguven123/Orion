package org.murat.accountservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.accountservice.config.KafkaConfig;
import org.murat.accountservice.config.RabbitMQConfig;
import org.murat.accountservice.entity.OutboxEvent;
import org.murat.accountservice.repository.OutboxEventRepository;
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
            "AccountDebitedEvent", RabbitMQConfig.ROUTING_KEY_ACCOUNT_DEBITED,
            "AccountCreditedEvent", RabbitMQConfig.ROUTING_KEY_ACCOUNT_CREDITED
    );

    private static final Map<String, String> TYPE_ID_MAP = Map.of(
            "AccountDebitedEvent", "org.murat.accountservice.Event.AccountDebitedEvent",
            "AccountCreditedEvent", "org.murat.accountservice.Event.AccountCreditedEvent"
    );

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Account Outbox scheduler: {} adet bekleyen event bulundu", pendingEvents.size());

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
                        RabbitMQConfig.INTERNAL_EXCHANGE,
                        routingKey,
                        message
                );
                publishToRabbitMQ(event);
                publishToKafka(event);

                event.setProcessed(true);
                outboxEventRepository.save(event);

                log.info("Account outbox event published: id={}, type={}, routingKey={}",
                        event.getId(), event.getEventType(), routingKey);

            } catch (Exception e) {
                log.error("Account outbox event gönderilemedi: id={}, type={}, hata={}",
                        event.getId(), event.getEventType(), e.getMessage(), e);
            }
        }
    }
    private void publishToRabbitMQ(OutboxEvent event) throws Exception {
        String routingKey = ROUTING_KEY_MAP.getOrDefault(event.getEventType(), "unknown.event");
        String typeId = TYPE_ID_MAP.getOrDefault(event.getEventType(), "java.lang.Object");

        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setHeader("__TypeId__", typeId);

        Message message = new Message(event.getPayload().getBytes("UTF-8"), props);
        rabbitTemplate.convertAndSend(RabbitMQConfig.INTERNAL_EXCHANGE, routingKey, message);
    }
    private void publishToKafka(OutboxEvent event) {
        String key = event.getAggregateType() + "-" + event.getAggregateId();

        kafkaTemplate.send(KafkaConfig.TOPIC_ACCOUNT_EVENTS, key, event.getPayload())
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka'ya gönderilemedi: id={}, hata={}",
                                event.getId(), ex.getMessage());
                    } else {
                        log.debug("Kafka'ya gönderildi: id={}, partition={}, offset={}",
                                event.getId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
    }

