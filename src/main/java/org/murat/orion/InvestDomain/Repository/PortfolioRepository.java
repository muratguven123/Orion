package org.murat.orion.InvestDomain.Repository;

import org.murat.orion.InvestDomain.Entity.InvestType;
import org.murat.orion.InvestDomain.Entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
   Optional<Portfolio> findByUserIdAndType(Long userId, InvestType type);
}
