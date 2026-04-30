package org.murat.orion.invest_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    public static final String INTERNAL_EXCHANGE = "internal.exchange";

    public static final String ROUTING_KEY_INVEST_BUY = "notification.invest.buy";
    public static final String ROUTING_KEY_INVEST_SELL = "notification.invest.sell";

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(INTERNAL_EXCHANGE, true, false);
    }
}

