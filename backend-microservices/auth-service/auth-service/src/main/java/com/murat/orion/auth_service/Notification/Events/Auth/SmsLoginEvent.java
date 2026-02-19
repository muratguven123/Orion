package com.murat.orion.auth_service.Notification.Events.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsLoginEvent {
    private Long userId;
    private String email;
    private String phoneNumber;
    private String subject;
    private String ipAddress;
    private String deviceInfo;
    private LocalDateTime loginAt;
}
