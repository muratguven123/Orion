package org.murat.orion.Notification.Events.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifiedEvent {
    private Long userId;
    private String phoneNumber;
    private String email;
    private String otpType;
    private LocalDateTime verifiedAt;
}
