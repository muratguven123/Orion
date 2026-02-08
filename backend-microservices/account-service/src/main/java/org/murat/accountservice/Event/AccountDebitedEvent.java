package org.murat.accountservice.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class AccountDebitedEvent {
    long userId;
    BigDecimal amount;
    String message;
}
