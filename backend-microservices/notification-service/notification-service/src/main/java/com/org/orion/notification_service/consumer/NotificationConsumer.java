package com.org.orion.notification_service.consumer;

import com.org.orion.notification_service.Dto.AccountDebitedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumer {
    @RabbitListener(queues = "notification-queue")
    public void consumeAccountDebitedEvent(AccountDebitedEvent event) {
        log.info(" MESAJ ALINDI: UserID: {}, Tutar: {}, Mesaj: {}",
                event.getUserId(), event.getAmount(), event.getMessage());
        log.info("SMS Gönderiliyor... [BAŞARILI]");
    }
}
