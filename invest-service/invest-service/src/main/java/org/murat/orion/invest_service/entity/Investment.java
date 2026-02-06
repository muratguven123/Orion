package org.murat.orion.invest_service.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "investments")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Convert(converter = InvestTypeConverter.class)
    @Column(name = "transaction_type")
    private InvestType investType;

    @Column(name = "price", precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Investment() {}

    public Investment(Long id, Long userId, String symbol, InvestType investType,
                      BigDecimal price, BigDecimal quantity, BigDecimal amount, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.symbol = symbol;
        this.investType = investType;
        this.price = price;
        this.quantity = quantity;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public InvestType getInvestType() { return investType; }
    public void setInvestType(InvestType investType) { this.investType = investType; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder pattern
    public static InvestmentBuilder builder() {
        return new InvestmentBuilder();
    }

    public static class InvestmentBuilder {
        private Long id;
        private Long userId;
        private String symbol;
        private InvestType investType;
        private BigDecimal price;
        private BigDecimal quantity;
        private BigDecimal amount;
        private LocalDateTime createdAt;

        public InvestmentBuilder id(Long id) { this.id = id; return this; }
        public InvestmentBuilder userId(Long userId) { this.userId = userId; return this; }
        public InvestmentBuilder symbol(String symbol) { this.symbol = symbol; return this; }
        public InvestmentBuilder investType(InvestType investType) { this.investType = investType; return this; }
        public InvestmentBuilder price(BigDecimal price) { this.price = price; return this; }
        public InvestmentBuilder quantity(BigDecimal quantity) { this.quantity = quantity; return this; }
        public InvestmentBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public InvestmentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Investment build() {
            return new Investment(id, userId, symbol, investType, price, quantity, amount, createdAt);
        }
    }
}
