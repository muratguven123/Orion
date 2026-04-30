package com.murat.orion.auth_service.AuthDomain.Events;

import java.time.LocalDateTime;

public record OtpSentEvent(
        Long userId,
        String phoneNumber,
        String email,
        String otpCode,
        String otpType,
        LocalDateTime sentAt,
        LocalDateTime expiresAt
) {
}
