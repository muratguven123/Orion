package org.murat.orion.Notification.Listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.Notification.Events.Auth.*;
import org.murat.orion.Notification.Service.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class AuthEventListener {
    private final NotificationService notificationService;
    @Async
    @EventListener
    public void onUserLoginFailedEvent(LoginFailedEvent event) {
        log.info("Kullanıcı giriş başarısız etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınıza yapılan başarısız giriş denemesi tespit edildi.",
                event.getEmail());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onUserLoginSuccessEvent(UserLoginEvent event) {
        log.info("Kullanıcı giriş başarılı etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınıza başarıyla giriş yapıldı.",
                event.getEmail());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onPasswordChangedEvent(PasswordChangedEvent event) {
        log.info("Parola değişikliği etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınızın parolası başarıyla değiştirildi.",
                event.getEmail());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onPasswordResetEvent(PasswordChangedEvent event) {
        log.info("Parola sıfırlama etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınızın parolası başarıyla sıfırlandı.",
                event.getEmail());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onUserLogoutEvent(UserLogoutEvent event) {
        log.info("Kullanıcı çıkış etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınızdan başarıyla çıkış yapıldı.",
                event.getEmail());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onOtpSentEvent(UserLoginEvent event) {
        log.info("OTP gönderme etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınız için bir OTP gönderildi.",
                event.getEmail());
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onOtpVerifiedEvent(OtpVerifiedEvent event) {
        log.info("OTP doğrulama etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınız için gönderilen OTP başarıyla doğrulandı.",
                event.getEmail());
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void RegisteredEvent(UserRegisteredEvent event) {
        log.info("Kullanıcı kayıt etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınız başarıyla oluşturuldu. Hoş geldiniz!",
                event.getEmail());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
        notificationService.sendSms(event.getUserId(), event.getPhoneNumber(), message);
    }
    @Async
    @EventListener
    public void onEmailLogin(EmailLoginEvent event) {
        log.info("E-posta ile giriş etkinliği alındı: {}", event);
        String message = String.format("Sayın %s, hesabınıza e-posta ile başarıyla giriş yapıldı.",
                event.getEmail());
        notificationService.sendEmail(event.getUserId(), event.getEmail(), event.getSubject(), message);
    }
}
