package com.murat.orion.auth_service.AuthDomain.Publisher;

import com.murat.orion.auth_service.AuthDomain.Config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMqPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserRegisteredEvent(Object event) {
        log.info("Publishing user registered event");
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.AUTH_EXCHANGE,
                RabbitMqConfig.USER_REGISTERED_ROUTING_KEY,
                event
        );
    }

    public void publishUserLoginEvent(Object event) {
        log.info("Publishing user login event");
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.AUTH_EXCHANGE,
                RabbitMqConfig.USER_LOGIN_ROUTING_KEY,
                event
        );
    }

    public void publishUserLogoutEvent(Object event) {
        log.info("Publishing user logout event");
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.AUTH_EXCHANGE,
                RabbitMqConfig.USER_LOGOUT_ROUTING_KEY,
                event
        );
    }

    public void publishPasswordChangedEvent(Object event) {
        log.info("Publishing password changed event");
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.AUTH_EXCHANGE,
                RabbitMqConfig.PASSWORD_CHANGED_ROUTING_KEY,
                event
        );
    }

    public void publishOtpSentEvent(Object event) {
        log.info("Publishing OTP sent event");
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.AUTH_EXCHANGE,
                RabbitMqConfig.OTP_SENT_ROUTING_KEY,
                event
        );
    }

    public void publishOtpVerifiedEvent(Object event) {
        log.info("Publishing OTP verified event");
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.AUTH_EXCHANGE,
                RabbitMqConfig.OTP_VERIFIED_ROUTING_KEY,
                event
        );
    }

    public void publishEmailLoginEvent(Object event) {
        log.info("Publishing email login event");
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.AUTH_EXCHANGE,
                RabbitMqConfig.EMAIL_LOGIN_ROUTING_KEY,
                event
        );
    }

    public void publishSmsLoginEvent(Object event) {
        log.info("Publishing SMS login event");
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.AUTH_EXCHANGE,
                RabbitMqConfig.SMS_LOGIN_ROUTING_KEY,
                event
        );
    }

    public void publishLoginFailedEvent(Object event) {
        log.info("Publishing login failed event");
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.AUTH_EXCHANGE,
                RabbitMqConfig.LOGIN_FAILED_ROUTING_KEY,
                event
        );
    }

    public void publishEvent(String exchange, String routingKey, Object event) {
        log.info("Publishing event to exchange: {}, routingKey: {}", exchange, routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}

