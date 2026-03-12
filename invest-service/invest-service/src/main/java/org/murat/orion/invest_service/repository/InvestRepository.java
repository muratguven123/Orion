package org.murat.orion.invest_service.repository;

import org.murat.orion.invest_service.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByUserId(Long userId);

    List<Investment> findBySymbol(String symbol);
}
