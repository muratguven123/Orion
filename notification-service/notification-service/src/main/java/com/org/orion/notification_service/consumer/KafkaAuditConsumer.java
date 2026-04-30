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
            topics = "orion.account.events",
            groupId = "notification-service-audit"
    )
    public void consumeAccountEvents(
            ConsumerRecord<String, String> record,
            Acknowledgment ack) {

        try {
            log.info("Kafka'dan event alındı | topic={} | partition={} | offset={} | key={}",
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.key());


            JsonNode payload = objectMapper.readTree(record.value());

            log.info("Event içeriği: userId={}, amount={}, message={}",
                    payload.path("userId").asText(),
                    payload.path("amount").asText(),
                    payload.path("message").asText());
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Kafka mesajı işlenemedi: {}", e.getMessage(), e);
        }
    }
}
