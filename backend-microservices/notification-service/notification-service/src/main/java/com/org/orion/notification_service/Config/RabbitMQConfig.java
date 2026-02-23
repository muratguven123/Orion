package com.org.orion.notification_service.Config;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue notificationQueue() {
        return new Queue("notification.queue", true);
    }

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange("internal.exchange");
    }

    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange internalExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(internalExchange)
                .with("notification.#");
    }
    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("org.murat.accountservice.Event.AccountDebitedEvent",
                com.org.orion.notification_service.dto.AccountDebitedEvent.class);
        idClassMapping.put("com.murat.orion.auth_service.AuthDomain.Events.UserRegisteredEvent",
                com.org.orion.notification_service.dto.UserRegisteredEvent.class);
        idClassMapping.put("com.murat.orion.auth_service.AuthDomain.Events.OtpSentEvent",
                com.org.orion.notification_service.dto.OtpSentEvent.class);
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(idClassMapping);
        classMapper.setTrustedPackages("*");
        converter.setClassMapper(classMapper);
        return converter;
    }
}
