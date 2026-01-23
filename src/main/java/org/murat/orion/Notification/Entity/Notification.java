package org.murat.orion.Notification.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue
    @UuidGenerator
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "subject")
    private String subject;

    @Column(name = "message_body", columnDefinition = "TEXT")
    private String messageBody;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;

    @CreationTimestamp
    private LocalDateTime sentAt;
}

