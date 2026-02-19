package com.murat.orion.auth_service.AuthDomain.Listener;

import com.murat.orion.auth_service.AuthDomain.Config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMqListener {

    @RabbitListener(queues = RabbitMqConfig.USER_REGISTERED_QUEUE)
    public void handleUserRegisteredEvent(Object event) {
        log.info("Received user registered event: {}", event);
    }

    @RabbitListener(queues = RabbitMqConfig.USER_LOGIN_QUEUE)
    public void handleUserLoginEvent(Object event) {
        log.info("Received user login event: {}", event);

    }

    @RabbitListener(queues = RabbitMqConfig.USER_LOGOUT_QUEUE)
    public void handleUserLogoutEvent(Object event) {
        log.info("Received user logout event: {}", event);

    }

    @RabbitListener(queues = RabbitMqConfig.PASSWORD_CHANGED_QUEUE)
    public void handlePasswordChangedEvent(Object event) {
        log.info("Received password changed event: {}", event);

    }

    @RabbitListener(queues = RabbitMqConfig.OTP_SENT_QUEUE)
    public void handleOtpSentEvent(Object event) {
        log.info("Received OTP sent event: {}", event);

    }

    @RabbitListener(queues = RabbitMqConfig.OTP_VERIFIED_QUEUE)
    public void handleOtpVerifiedEvent(Object event) {
        log.info("Received OTP verified event: {}", event);

    }

    @RabbitListener(queues = RabbitMqConfig.EMAIL_LOGIN_QUEUE)
    public void handleEmailLoginEvent(Object event) {
        log.info("Received email login event: {}", event);

    }

    @RabbitListener(queues = RabbitMqConfig.SMS_LOGIN_QUEUE)
    public void handleSmsLoginEvent(Object event) {
        log.info("Received SMS login event: {}", event);
    }

    @RabbitListener(queues = RabbitMqConfig.LOGIN_FAILED_QUEUE)
    public void handleLoginFailedEvent(Object event) {
        log.info("Received login failed event: {}", event);

    }

    @RabbitListener(queues = RabbitMqConfig.DEAD_LETTER_QUEUE)
    public void handleDeadLetterMessage(Object message) {
        log.warn("Received dead letter message: {}", message);
    }
}

