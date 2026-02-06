package org.murat.orion.payment_service.service;

import lombok.extern.slf4j.Slf4j;
import org.murat.orion.payment_service.dto.request.PaymentRequest;
import org.murat.orion.payment_service.dto.request.PaymentSearchRequest;
import org.murat.orion.payment_service.dto.request.PaymentTransferRequest;
import org.murat.orion.payment_service.entity.Payment;
import org.murat.orion.payment_service.entity.PaymentTransactionStatus;
import org.murat.orion.payment_service.entity.PaymentTransactionType;
import org.murat.orion.payment_service.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    // private final AccountIntegrationService accountIntegrationService;
    private final Map<PaymentTransactionType, PaymentStrategy> paymentStrategyMap;

    public PaymentService(// AccountIntegrationService accountIntegrationService,
                          PaymentRepository paymentRepository,
                          List<PaymentStrategy> paymentStrategies) {
        // this.accountIntegrationService = accountIntegrationService;
        this.paymentRepository = paymentRepository;
        this.paymentStrategyMap = paymentStrategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getPaymentType, Function.identity()));
    }

//    @Transactional
//    public void process(PaymentRequest paymentRequest) {
//        PaymentStrategy paymentStrategy = paymentStrategyMap.get(paymentRequest.getPaymentType());
//        if (paymentStrategy == null) {
//            throw new IllegalArgumentException("Unsupported payment type: " + paymentRequest.getPaymentType());
//        }
//        paymentStrategy.processPayment(paymentRequest);
//    }

    @Transactional
    public void deposit(long accountId, Double amount, String currency, String email, String phoneNumber) {
        Payment payment = Payment.builder()
                .targetAccountId(accountId)
                .amount(BigDecimal.valueOf(amount))
                .currency(currency)
                .type(PaymentTransactionType.DEPOSIT)
                .status(PaymentTransactionStatus.SUCCESS)
                .referenceCode(UUID.randomUUID().toString())
                .description("Para Yatırma İşlemi")
                .build();
        paymentRepository.save(payment);
        // accountIntegrationService.credit(accountId, amount, email, phoneNumber);
        log.info("Deposited payment for account {} with amount {}", accountId, amount);
    }

    @Transactional
    public void withdraw(Long accountId, Double amount, String currency, String email, String phoneNumber) {
        Payment transaction = Payment.builder()
                .sourceAccountId(accountId)
                .amount(BigDecimal.valueOf(amount))
                .currency(currency)
                .type(PaymentTransactionType.WITHDRAW)
                .status(PaymentTransactionStatus.SUCCESS)
                .referenceCode(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .description("ATM Para Çekme")
                .build();

        paymentRepository.save(transaction);
        // accountIntegrationService.debit(accountId, amount, email, phoneNumber);
        log.info("Para çekildi. Account: {}, Tutar: {}", accountId, amount);
    }

//    public Page<PaymentTransferRequest> getAccountHistory(UUID accountId, Pageable pageable) {
//        return paymentRepository.findAllByAccountId(accountId, pageable)
//                .map(this::mapToDto);
//    }
//
//    public Page<PaymentSearchRequest> searchRequests(PaymentSearchRequest request, Pageable pageable) {
//        Specification<Payment> spec = PaymentSpecification.getFilteredPayments(request);
//        return paymentRepository.findAll(spec, pageable)
//                .map(this::mapToSearchDto);
//    }

    private PaymentTransferRequest mapToDto(Payment entity) {
        return PaymentTransferRequest.builder()
                .referenceCode(entity.getReferenceCode())
                .amount(entity.getAmount().doubleValue())
                .currency(entity.getCurrency())
                .type(entity.getType())
                .status(entity.getStatus())
                .date(entity.getCreatedAt())
                .description(entity.getDescription())
                .sourceAccount(entity.getSourceAccountId() != null ? entity.getSourceAccountId().toString() : "ATM/BANKA")
                .targetAccount(entity.getTargetAccountId() != null ? entity.getTargetAccountId().toString() : "ATM/BANKA")
                .build();
    }

    private PaymentSearchRequest mapToSearchDto(Payment entity) {
        return PaymentSearchRequest.builder()
                .type(entity.getType())
                .currency(entity.getCurrency())
                .minAmount(entity.getAmount().doubleValue())
                .status(entity.getStatus().name())
                .startDate(entity.getCreatedAt())
                .endDate(entity.getCreatedAt())
                .build();
    }
}
