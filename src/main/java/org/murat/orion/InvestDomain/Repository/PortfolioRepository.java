package org.murat.orion.InvestDomain.Repository;

import org.murat.orion.InvestDomain.Entity.InvestType;
import org.murat.orion.InvestDomain.Entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
   Optional<Portfolio> findByUserIdAndSymbol(Long userId, String symbol);
    List<Portfolio> findByUserId(Long userId);
}
