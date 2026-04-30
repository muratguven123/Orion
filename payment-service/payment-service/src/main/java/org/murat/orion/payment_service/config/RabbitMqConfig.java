package org.murat.orion.payment_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    public static final String INTERNAL_EXCHANGE = "internal.exchange";

    public static final String ROUTING_KEY_PAYMENT_DEPOSIT = "notification.payment.deposit";
    public static final String ROUTING_KEY_PAYMENT_WITHDRAW = "notification.payment.withdraw";
    public static final String ROUTING_KEY_PAYMENT_TRANSFER = "notification.payment.transfer";

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(INTERNAL_EXCHANGE, true, false);
    }
}

