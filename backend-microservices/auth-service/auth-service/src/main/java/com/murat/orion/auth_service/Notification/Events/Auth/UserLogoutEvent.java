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
public class UserLogoutEvent {
    private Long userId;
    private String email;
    private String phoneNumber;
    private String subject;
    private LocalDateTime logoutAt;
}
