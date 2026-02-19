package com.murat.orion.auth_service.AuthDomain.Config;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMqErrorHandler implements RabbitListenerErrorHandler {

    @Override
    public Object handleError(Message message, Channel channel, org.springframework.messaging.Message<?> message1, ListenerExecutionFailedException e) throws Exception {
        log.error("RabbitMQ message processing error occurred", e);
        log.error("Message body: {}", new String(message.getBody()));
        log.error("Exception cause: {}", e.getCause().getMessage());
        return null;
    }
}

