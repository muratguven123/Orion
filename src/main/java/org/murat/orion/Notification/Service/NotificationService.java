package org.murat.orion.Notification.Service;

import lombok.extern.slf4j.Slf4j;
import org.murat.orion.Notification.Entity.Notification;
import org.murat.orion.Notification.Entity.NotificationType;
import org.murat.orion.Notification.Repository.notificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@EnableAsync
public class NotificationService {
    private final notificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final SmsNotificationService smsNotificationService;
    private final NotificationLogService notificationLogService;
    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Autowired
    public NotificationService(
            notificationRepository notificationRepository,
            @Autowired(required = false) JavaMailSender mailSender,
            SmsNotificationService smsNotificationService, NotificationLogService notificationLogService) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
        this.smsNotificationService = smsNotificationService;
        this.notificationLogService = notificationLogService;
    }

    @Async
    public void sendEmail(Long userId, String email, String subject, String content) {
        boolean isSent = false;
        try {

            if (mailSender != null) {;
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(email);
                message.setSubject(subject);
                message.setText(content);
                mailSender.send(message);
                isSent = true;
                log.info("Mail Gönderildi kime {}", email);
                ((JavaMailSenderImpl) mailSender).getJavaMailProperties().put("mail.debug", "true");
            }
        } catch (Exception e) {
            log.error("E-posta gönderilirken hata oluştu: {}", e.getMessage());
        }finally {
            notificationLogService.saveLog(userId, email, subject, content, NotificationType.EMAIL, isSent);
        }
    }

    @Async
    public void sendSms(Long userıd, String phoneNumber, String message) {
        smsNotificationService.sendSms(userıd, phoneNumber, message);
        saveLogforSms(userıd, phoneNumber, message);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void saveLogforSms(Long userıd, String phoneNumber, String message) {
        Notification history = Notification.builder()
                .userId(userıd)
                .phoneNumber(phoneNumber)
                .messageBody(message)
                .type(NotificationType.SMS)
                .build();
        notificationRepository.save(history);
    }
}