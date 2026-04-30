package org.murat.accountservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    public static final String TOPIC_ACCOUNT_EVENTS = "orion.account.events";

    @Bean
    public NewTopic accountEvents() {
        return TopicBuilder.name(TOPIC_ACCOUNT_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

}
