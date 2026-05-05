package org.murat.orion.invest_service.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    public static final String TOPIC_İNVEST_EVENTS = "orion.invest.events";
    @Bean
    public NewTopic investEvents() {
        return TopicBuilder.name(TOPIC_İNVEST_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}