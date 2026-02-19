package com.murat.orion.auth_service.AuthDomain.Events;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserLoginEvent {
    private LocalDateTime loginAt;
    private String deviceInfo;
    private String ipAddress;
    private String email;
    private String phoneNumber;
    private String subject;
    private Long userId;
}