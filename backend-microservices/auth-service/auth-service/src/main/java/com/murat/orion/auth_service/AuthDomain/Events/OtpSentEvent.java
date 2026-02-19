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
public class OtpSentEvent {
    private Long userId;
    private String phoneNumber;
    private String email;
    private String subject;
    private String otpType;
    private LocalDateTime sentAt;
    private LocalDateTime expiresAt;
}
