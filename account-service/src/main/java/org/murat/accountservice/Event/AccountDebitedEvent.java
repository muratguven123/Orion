package org.murat.accountservice.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
