package com.org.orion.notification_service.Dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountDebitedEvent {
    private Long userId;
    private BigDecimal amount;
    private String message;
}

