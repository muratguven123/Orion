package org.murat.orion.invest_service.repository;

import org.murat.orion.invest_service.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByUserIdAndSymbol(Long userId, String symbol);
    List<Portfolio> findByUserId(Long userId);
}
