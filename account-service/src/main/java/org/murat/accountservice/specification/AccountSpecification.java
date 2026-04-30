package org.murat.accountservice.specification;

import org.murat.accountservice.dto.Request.AccountSearchRequest;
import org.murat.accountservice.entity.Account;
import org.springframework.data.jpa.domain.Specification;

public class AccountSpecification {

    public static Specification<Account> getFilteredPayments(AccountSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (request.getAccountName() != null && !request.getAccountName().isBlank()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("accountName")),
                                "%" + request.getAccountName().toLowerCase() + "%"));
            }

            if (request.getAccountType() != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("accountType"), request.getAccountType()));
            }

            if (request.getCurrency() != null && !request.getCurrency().isBlank()) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("currency"), request.getCurrency()));
            }

            return predicates;
        };
    }
}
