package com.org.orion.notification_service.Config;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Queue;
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
                .bind((org.springframework.amqp.core.Queue) notificationQueue)
                .to(internalExchange)
                .with("notification.#");
    }
}
