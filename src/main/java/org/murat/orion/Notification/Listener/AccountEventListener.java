package org.murat.orion.Notification.Listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.AccountDomain.Service.AccountService;
import org.murat.orion.Notification.Events.Account.AccountCreatedEvent;
import org.murat.orion.Notification.Events.Account.AccountDeletedEvent;
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
    public void onAccountCreated(AccountCreatedEvent event,String email){
        log.info("Hesap oluşturma etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınız başarıyla oluşturuldu! Hesap Numaranız: %s",
                event.getAccountName(), event.getAccountNumber());
        notificationService.sendEmail();
    }
    @Async
    @EventListener
    public void onAccountDeleted(AccountDeletedEvent event){
        log.info("Hesap silme etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınız başarıyla silindi! Hesap Numaranız: %s",
                event.getAccountName(), event.getAccountNumber());
        notificationService.sendEmail(event.getAccountId(), event,event.getAccountNumber(), message);

    }

}
