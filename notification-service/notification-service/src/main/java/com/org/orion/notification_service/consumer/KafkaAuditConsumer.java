package com.org.orion.notification_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaAuditConsumer {
    private final ObjectMapper objectMapper;
    @KafkaListener(
            topics = {"orion.account.events", "orion.payment.events", "orion.invest.events"},
            groupId = "notification-service-audit"
    )
    public void consumeAllServiceEvents(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            log.info("Kafka event alındı | topic={} | partition={} | offset={} | key={}",
                    record.topic(), record.partition(), record.offset(), record.key());
            JsonNode payload = objectMapper.readTree(record.value());
            switch (record.topic()) {
                case "orion.payment.events" -> handlePaymentEvent(payload);
                case "orion.invest.events"  -> handleInvestEvent(payload);
                default                     -> handleAccountEvent(payload);
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Kafka mesajı işlenemedi: {}", e.getMessage(), e);
        }
    }
}
