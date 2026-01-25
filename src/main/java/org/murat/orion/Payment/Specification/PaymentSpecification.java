package org.murat.orion.Payment.Specification;

import jakarta.persistence.criteria.Predicate;
import org.murat.orion.Payment.Dto.Request.PaymentSearchRequest;
import org.murat.orion.Payment.Entity.Payment;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PaymentSpecification {

    public static Specification<Payment> getFilteredPayments(PaymentSearchRequest request) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.getType()));
            }
            if (request.getCurrency() != null && !request.getCurrency().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("currency"), request.getCurrency()));
            }
            if (request.getMinAmount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), request.getMinAmount()));
            }
            if (request.getMaxAmount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), request.getMaxAmount()));
            }
            if (request.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.getStartDate()));
            }
            if (request.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), request.getEndDate()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
