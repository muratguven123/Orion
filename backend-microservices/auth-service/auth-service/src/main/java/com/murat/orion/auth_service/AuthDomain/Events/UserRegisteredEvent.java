package com.murat.orion.auth_service.AuthDomain.Events;

import java.time.LocalDateTime;

public record UserRegisteredEvent(
        Long userId,
        String email,
        String phoneNumber,
        String firstName,
        String lastName,
        LocalDateTime registeredAt
) {
}
