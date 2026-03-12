package org.murat.orion.invest_service.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios")
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Convert(converter = InvestTypeConverter.class)
    @Column(name = "type")
    private InvestType type;

    @Column(name = "quantity", precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "average_cost", precision = 19, scale = 2)
    private BigDecimal averageCost;

    @Version
    private Long version;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Portfolio() {}

    public Portfolio(Long id, Long userId, String symbol, InvestType type,
                     BigDecimal quantity, BigDecimal averageCost, Long version, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.version = version;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public InvestType getType() { return type; }
    public void setType(InvestType type) { this.type = type; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getAverageCost() { return averageCost; }
    public void setAverageCost(BigDecimal averageCost) { this.averageCost = averageCost; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder pattern
    public static PortfolioBuilder builder() {
        return new PortfolioBuilder();
    }

    public static class PortfolioBuilder {
        private Long id;
        private Long userId;
        private String symbol;
        private InvestType type;
        private BigDecimal quantity;
        private BigDecimal averageCost;
        private Long version;
        private LocalDateTime updatedAt;

        public PortfolioBuilder id(Long id) { this.id = id; return this; }
        public PortfolioBuilder userId(Long userId) { this.userId = userId; return this; }
        public PortfolioBuilder symbol(String symbol) { this.symbol = symbol; return this; }
        public PortfolioBuilder type(InvestType type) { this.type = type; return this; }
        public PortfolioBuilder quantity(BigDecimal quantity) { this.quantity = quantity; return this; }
        public PortfolioBuilder averageCost(BigDecimal averageCost) { this.averageCost = averageCost; return this; }
        public PortfolioBuilder version(Long version) { this.version = version; return this; }
        public PortfolioBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Portfolio build() {
            return new Portfolio(id, userId, symbol, type, quantity, averageCost, version, updatedAt);
        }
    }
}
