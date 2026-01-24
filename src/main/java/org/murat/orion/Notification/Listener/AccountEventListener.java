package org.murat.orion.Notification.Listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.Notification.Events.Account.*;
import org.murat.orion.Notification.Service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class AccountEventListener {
    private final NotificationService notificationService;

    @Async
    @EventListener
    public void onAccountCreated(AccountCreatedEvent event) {
        log.info("Hesap oluşturma etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınız başarıyla oluşturuldu! Hesap Numaranız: %s",
                event.getAccountName(), event.getAccountNumber());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }

    @Async
    @EventListener
    public void onAccountDeleted(AccountDeletedEvent event) {
        log.info("Hesap silme etkinliği alındı: {}", event);
        String message = String.format("Hesabınız başarıyla silindi! Hesap Numaranız: %s",
                event.getAccountNumber());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }

    @Async
    @EventListener
    public void onAccountUpdated(AccountUpdatedEvent event) {
        log.info("Hesap güncelleme etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınız başarıyla güncellendi!",
                event.getAccountName());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onAccountDeactivated(AccountDeactivatedEvent event) {
        log.info("Hesap devre dışı bırakma etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınız devre dışı bırakıldı! Sebep: %s",
                event.getAccountNumber(), event.getReason());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onAccountActivatedt(AccountActivatedEvent event) {
        log.info("Hesap etkinleştirme etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınız etkinleştirildi!",
                event.getAccountNumber());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onAccountDebited(AccountDebitedEvent event) {
        log.info("Hesap borçlandırma etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınızdan %s %s tutarında bir borçlandırma yapıldı. Yeni bakiyeniz: %s %s",
                event.getAccountId(), event.getAmount(), event.getCurrency(), event.getNewBalance(), event.getCurrency());
        notificationService.sendEmail(event.getAccountId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getAccountId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onAccountCredited(AccountCreditedEvent event) {
        log.info("Hesap alacaklandırma etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınıza %s %s tutarında bir alacaklandırma yapıldı. Yeni bakiyeniz: %s %s",
                event.getAccountId(), event.getAmount(), event.getCurrency(), event.getNewBalance(), event.getCurrency());
        notificationService.sendEmail(event.getAccountId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getAccountId(), event.getPhoneNumber(), message);
    }
}
