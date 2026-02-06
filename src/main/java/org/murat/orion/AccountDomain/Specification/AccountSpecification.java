package org.murat.orion.AccountDomain.Specification;

import jakarta.persistence.criteria.Predicate;
import org.murat.orion.AccountDomain.Dto.Request.AccountSearchRequest;
import org.murat.orion.AccountDomain.Entity.Account;
import org.murat.orion.Payment.Dto.Request.PaymentSearchRequest;
import org.murat.orion.Payment.Entity.Payment;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AccountSpecification {
    public static Specification<Account> getFilteredPayments(AccountSearchRequest request) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getAccountName() != null && !request.getAccountName().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("accountName"), request.getAccountName()));
            }
            if (request.getAccountType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("accountType"), request.getAccountType()));
            }
            if (request.getCurrency() != null && !request.getCurrency().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("currency"), request.getCurrency()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
