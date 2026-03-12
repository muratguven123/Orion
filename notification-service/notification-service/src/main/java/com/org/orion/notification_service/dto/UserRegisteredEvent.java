package com.org.orion.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent implements Serializable {
    private Long userId;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private LocalDateTime registeredAt;
}
