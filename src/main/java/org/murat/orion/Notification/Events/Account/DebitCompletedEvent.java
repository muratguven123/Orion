package org.murat.orion.Notification.Events.Account;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
@Data
@RequiredArgsConstructor
public class DebitCompletedEvent {
    Long accountId;
    BigDecimal amount;
    String email;
    String phoneNumber;
}
