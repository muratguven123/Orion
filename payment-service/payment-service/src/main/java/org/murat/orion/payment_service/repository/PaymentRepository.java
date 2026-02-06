package org.murat.orion.payment_service.repository;

import org.murat.orion.payment_service.entity.Payment;
import org.murat.orion.payment_service.entity.PaymentTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findBySourceAccountId(Long sourceAccountId);

    List<Payment> findByTargetAccountId(Long targetAccountId);

    Optional<Payment> findByReferenceCode(String referenceCode);

    List<Payment> findByStatus(PaymentTransactionStatus status);

    List<Payment> findBySourceAccountIdAndStatus(Long sourceAccountId, PaymentTransactionStatus status);
}
