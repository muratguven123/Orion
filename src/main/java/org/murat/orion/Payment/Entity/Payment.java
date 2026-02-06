package org.murat.orion.Payment.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payments", schema = "payment", indexes = {
        @Index(name = "idx_tx_source_acc", columnList = "source_account_id"),
        @Index(name = "idx_tx_target_acc", columnList = "target_account_id"),
        @Index(name = "idx_tx_ref_code", columnList = "reference_code")
})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "source_account_id")
    private Long sourceAccountId;

    @Column(name = "target_account_id")
    private Long targetAccountId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;


    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaymentTransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentTransactionStatus status;


    @Column(name = "reference_code", unique = true, nullable = false)
    private String referenceCode;


    @Column(name = "description")
    private String description;

    @Column(name = "failure_reason")
    private String failureReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}
