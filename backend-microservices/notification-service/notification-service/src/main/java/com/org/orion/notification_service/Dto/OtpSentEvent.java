package com.org.orion.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpSentEvent implements Serializable {
    private Long userId;
    private String phoneNumber;
    private String email;
    private String otpCode;
    private String otpType;
    private LocalDateTime sentAt;
    private LocalDateTime expiresAt;
}
