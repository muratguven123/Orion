package com.murat.orion.auth_service.AuthDomain.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {


    public static final String AUTH_EXCHANGE = "auth.exchange";
    public static final String DEAD_LETTER_EXCHANGE = "auth.dead.letter.exchange";

    public static final String USER_REGISTERED_QUEUE = "auth.user.registered.queue";
    public static final String USER_LOGIN_QUEUE = "auth.user.login.queue";
    public static final String USER_LOGOUT_QUEUE = "auth.user.logout.queue";
    public static final String PASSWORD_CHANGED_QUEUE = "auth.password.changed.queue";
    public static final String OTP_SENT_QUEUE = "auth.otp.sent.queue";
    public static final String OTP_VERIFIED_QUEUE = "auth.otp.verified.queue";
    public static final String EMAIL_LOGIN_QUEUE = "auth.email.login.queue";
    public static final String SMS_LOGIN_QUEUE = "auth.sms.login.queue";
    public static final String LOGIN_FAILED_QUEUE = "auth.login.failed.queue";


    public static final String USER_REGISTERED_ROUTING_KEY = "auth.user.registered";
    public static final String USER_LOGIN_ROUTING_KEY = "auth.user.login";
    public static final String USER_LOGOUT_ROUTING_KEY = "auth.user.logout";
    public static final String PASSWORD_CHANGED_ROUTING_KEY = "auth.password.changed";
    public static final String OTP_SENT_ROUTING_KEY = "auth.otp.sent";
    public static final String OTP_VERIFIED_ROUTING_KEY = "auth.otp.verified";
    public static final String EMAIL_LOGIN_ROUTING_KEY = "auth.email.login";
    public static final String SMS_LOGIN_ROUTING_KEY = "auth.sms.login";
    public static final String LOGIN_FAILED_ROUTING_KEY = "auth.login.failed";


    public static final String DEAD_LETTER_QUEUE = "auth.dead.letter.queue";
    public static final String DEAD_LETTER_ROUTING_KEY = "auth.dead.letter";



    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(AUTH_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DEAD_LETTER_EXCHANGE, true, false);
    }



    @Bean
    public Queue userRegisteredQueue() {
        return QueueBuilder.durable(USER_REGISTERED_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue userLoginQueue() {
        return QueueBuilder.durable(USER_LOGIN_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue userLogoutQueue() {
        return QueueBuilder.durable(USER_LOGOUT_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue passwordChangedQueue() {
        return QueueBuilder.durable(PASSWORD_CHANGED_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue otpSentQueue() {
        return QueueBuilder.durable(OTP_SENT_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue otpVerifiedQueue() {
        return QueueBuilder.durable(OTP_VERIFIED_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue emailLoginQueue() {
        return QueueBuilder.durable(EMAIL_LOGIN_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue smsLoginQueue() {
        return QueueBuilder.durable(SMS_LOGIN_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue loginFailedQueue() {
        return QueueBuilder.durable(LOGIN_FAILED_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DEAD_LETTER_QUEUE, true);
    }



    @Bean
    public Binding userRegisteredBinding(Queue userRegisteredQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(userRegisteredQueue)
                .to(authExchange)
                .with(USER_REGISTERED_ROUTING_KEY);
    }

    @Bean
    public Binding userLoginBinding(Queue userLoginQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(userLoginQueue)
                .to(authExchange)
                .with(USER_LOGIN_ROUTING_KEY);
    }

    @Bean
    public Binding userLogoutBinding(Queue userLogoutQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(userLogoutQueue)
                .to(authExchange)
                .with(USER_LOGOUT_ROUTING_KEY);
    }

    @Bean
    public Binding passwordChangedBinding(Queue passwordChangedQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(passwordChangedQueue)
                .to(authExchange)
                .with(PASSWORD_CHANGED_ROUTING_KEY);
    }

    @Bean
    public Binding otpSentBinding(Queue otpSentQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(otpSentQueue)
                .to(authExchange)
                .with(OTP_SENT_ROUTING_KEY);
    }

    @Bean
    public Binding otpVerifiedBinding(Queue otpVerifiedQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(otpVerifiedQueue)
                .to(authExchange)
                .with(OTP_VERIFIED_ROUTING_KEY);
    }

    @Bean
    public Binding emailLoginBinding(Queue emailLoginQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(emailLoginQueue)
                .to(authExchange)
                .with(EMAIL_LOGIN_ROUTING_KEY);
    }

    @Bean
    public Binding smsLoginBinding(Queue smsLoginQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(smsLoginQueue)
                .to(authExchange)
                .with(SMS_LOGIN_ROUTING_KEY);
    }

    @Bean
    public Binding loginFailedBinding(Queue loginFailedQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(loginFailedQueue)
                .to(authExchange)
                .with(LOGIN_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DEAD_LETTER_ROUTING_KEY);
    }
}

