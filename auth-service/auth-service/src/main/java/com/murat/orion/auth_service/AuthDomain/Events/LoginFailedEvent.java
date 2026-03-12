package com.murat.orion.auth_service.AuthDomain.Events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginFailedEvent {
    private Long userId;
    private String email;
    private String phoneNumber;
    private String subject;
    private String ipAddress;
    private String reason;
    private LocalDateTime failedAt;
}
