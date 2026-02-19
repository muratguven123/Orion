package com.murat.orion.auth_service.AuthDomain.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String phoneNumber;
    private LocalDateTime loginTime;
    private String status;
}
