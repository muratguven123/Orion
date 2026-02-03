package org.murat.orion.InvestDomain.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "ınvest", schema = "invest_domain")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invesment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private InvestType i̇nvestType;

    @Column(name = "price", precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
