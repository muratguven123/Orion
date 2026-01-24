package org.murat.orion.Notification.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.Notification.Entity.Notification;
import org.murat.orion.Notification.Entity.NotificationType;
import org.murat.orion.Notification.Repository.notificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class NotificationService {
    private final notificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final SmsNotificationService smsNotificationService;
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Async
    public void sendEmail(Long userId, String email, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("Mail Gönderildi kime {}, mail {} " + email, email);
        } catch (Exception e) {
            log.error("Mail Gönderimi Sırasında Hata Meydana Geldi :" + e.getMessage());
        }
    }
    private void saveLogforEmail(Long userId, String email, String subject, String content) {
        Notification history = Notification.builder()
                .userId(userId)
                .recipient(email)
                .subject(subject)
                .messageBody(content)
                .type(NotificationType.EMAIL)
                .build();

        notificationRepository.save(history);
    }
    public void sendSms(Long userıd, String phoneNumber, String message) {
        smsNotificationService.sendSms(userıd, phoneNumber, message);
    }
}