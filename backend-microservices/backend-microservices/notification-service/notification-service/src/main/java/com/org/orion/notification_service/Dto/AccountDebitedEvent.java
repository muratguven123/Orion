package com.org.orion.notification_service.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDebitedEvent implements Serializable {
    private Long userId;
    private BigDecimal amount;
    private String message;
}

