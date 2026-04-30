package org.murat.accountservice.repository;

import org.murat.accountservice.entity.Account;
import org.murat.accountservice.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Placeholder repository interface for package creation.
 */
@Repository
public interface AccountRepository  extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    List<Account> findByUserId(Long userId);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserIdAndStatus(Long userId, AccountStatus status);

    List<Account> findByUserIdAndIsActiveTrue(Long userId);

    boolean existsByAccountNumber(String accountNumber);}
