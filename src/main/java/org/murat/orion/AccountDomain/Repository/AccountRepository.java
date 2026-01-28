package org.murat.orion.AccountDomain.Repository;

import org.murat.orion.AccountDomain.Entity.Account;
import org.murat.orion.AccountDomain.Entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    List<Account> findByUserId(Long userId);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserIdAndStatus(Long userId, AccountStatus status);

    List<Account> findByUserIdAndIsActiveTrue(Long userId);

    boolean existsByAccountNumber(String accountNumber);
}
