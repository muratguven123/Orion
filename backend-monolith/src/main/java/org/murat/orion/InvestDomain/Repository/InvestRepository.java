package org.murat.orion.InvestDomain.Repository;

import org.murat.orion.InvestDomain.Entity.Invesment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestRepository extends JpaRepository<Invesment, Long> {

}