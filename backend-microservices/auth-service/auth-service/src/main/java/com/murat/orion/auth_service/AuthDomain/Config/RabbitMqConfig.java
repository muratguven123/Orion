package com.murat.orion.auth_service.AuthDomain.Config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    public static final String INTERNAL_EXCHANGE = "internal.exchange";

    public static final String ROUTING_KEY_USER_REGISTERED = "notification.auth.registered";
    public static final String ROUTING_KEY_OTP_SENT = "notification.auth.otp";

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(INTERNAL_EXCHANGE, true, false);
    }
}
