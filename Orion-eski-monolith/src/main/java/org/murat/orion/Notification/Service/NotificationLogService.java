package org.murat.orion.Notification.Service;

import lombok.RequiredArgsConstructor;
import org.murat.orion.Notification.Entity.Notification;
import org.murat.orion.Notification.Entity.NotificationType;
import org.murat.orion.Notification.Repository.notificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationLogService {
    private final notificationRepository notificationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(Long userId, String email,  String subject, String content, NotificationType type, boolean sent) {
        Notification history = Notification.builder()
                .userId(userId)
                .email(email)
                .subject(subject)
                .messageBody(content)
                .type(type)
                .sent(sent)
                .sentAt(sent ? LocalDateTime.now() : null)
                .build();
        notificationRepository.saveAndFlush(history);
    }

}
