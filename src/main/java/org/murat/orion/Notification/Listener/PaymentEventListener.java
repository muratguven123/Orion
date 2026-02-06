package org.murat.orion.Notification.Listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.Notification.Events.Payment.DepositCompletedEvent;
import org.murat.orion.Notification.Events.Payment.ProcessCompletedEvent;
import org.murat.orion.Notification.Service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class PaymentEventListener {
    private final NotificationService notificationService;

    @Async
    @EventListener
    public void onPaymentProcessed(ProcessCompletedEvent event) {
        log.info("Ödeme işleme etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, ödemeniz başarıyla işlendi! Ödeme Tutarı: %s",
                event.getPhoneNumber(), event.getAmount());
        notificationService.sendEmail(event.getTargetAccountId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getTargetAccountId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onPaymentProcessedFail(ProcessCompletedEvent event) {
        log.info("Ödeme işleme başarısız etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, ödemeniz işlenemedi! Ödeme Tutarı: %s",
                event.getPhoneNumber(), event.getAmount());
        notificationService.sendEmail(event.getTargetAccountId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getTargetAccountId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void  onDepositProcessed(ProcessCompletedEvent event) {
        log.info("Para yatırma işleme etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, para yatırma işleminiz başarıyla işlendi! Yatırılan Tutar: %s",
                event.getPhoneNumber(), event.getAmount());
        notificationService.sendEmail(event.getTargetAccountId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getTargetAccountId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void  onWithdrawProcessed(ProcessCompletedEvent event) {
        log.info("Para çekme işleme etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, para çekme işleminiz başarıyla işlendi! Çekilen Tutar: %s",
                event.getPhoneNumber(), event.getAmount());
        notificationService.sendEmail(event.getSourceAccountId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getSourceAccountId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void  onTransferProcessed(ProcessCompletedEvent event) {
        log.info("Para transferi işleme etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, para transferi işleminiz başarıyla işlendi! Transfer Edilen Tutar: %s",
                event.getPhoneNumber(), event.getAmount());
        notificationService.sendEmail(event.getSourceAccountId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getSourceAccountId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onDepositCompleted(DepositCompletedEvent event) {
        log.info("Para yatırma tamamlandı etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, para yatırma işleminiz başarıyla tamamlandı! Yatırılan Tutar: %s",
                event.getPhoneNumber(), event.getAmount());
        notificationService.sendEmail(event.getAccountId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getAccountId(), event.getPhoneNumber(), message);
    }
}
