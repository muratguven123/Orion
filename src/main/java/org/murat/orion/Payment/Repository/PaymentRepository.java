package org.murat.orion.Payment.Repository;

import org.murat.orion.Payment.Entity.Payment;
import org.murat.orion.Payment.Entity.PaymentTransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository  extends JpaRepository<Payment, Integer> {
@Query("Select p from Payment p  where p.sourceAccountId= :taccountId or p.targetAccountId=:taccountId order by p.createdAt desc")
Page<Payment> findAllByAccountId(@Param("accountId") UUID accountId, Pageable pageable);

Optional<Payment> findByReferenceCode(String referenceCode);

Page<Payment> findByTargetAccountIdOrderByCreatedAtDesc(UUID targetAccountId, Pageable pageable);

Page<Payment> findBySourceAccountIdOrderByCreatedAtDesc(UUID sourceAccountId, Pageable pageable);

@Query("SELECT SUM(t.amount) FROM Payment t WHERE t.sourceAccountId = :accountId AND t.createdAt > :startDate AND t.status = 'SUCCESS'")
BigDecimal calculateTotalWithdrawalSince(@Param("accountId") UUID accountId, @Param("startDate") LocalDateTime startDate);

List<Payment> findByStatusAndCreatedAtBefore(PaymentTransactionStatus status, LocalDateTime dateTime);
}
