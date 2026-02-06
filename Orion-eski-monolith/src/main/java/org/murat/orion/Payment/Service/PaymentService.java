package org.murat.orion.Payment.Service;

import lombok.extern.slf4j.Slf4j;
import org.murat.orion.Notification.Events.Payment.DepositCompletedEvent;
import org.murat.orion.Notification.Events.Payment.ProcessCompletedEvent;
import org.murat.orion.Notification.Events.Payment.WithDrawComplicatedEvent;
import org.murat.orion.Payment.Dto.Request.PaymentRequest;
import org.murat.orion.Payment.Dto.Request.PaymentSearchRequest;
import org.murat.orion.Payment.Dto.Request.PaymentTransferRequest;
import org.murat.orion.Payment.Entity.Payment;
import org.murat.orion.Payment.Entity.PaymentTransactionStatus;
import org.murat.orion.Payment.Entity.PaymentTransactionType;

import org.murat.orion.Payment.Repository.PaymentRepository;
import org.murat.orion.Payment.Specification.PaymentSpecification;
import org.murat.orion.Payment.İnterfaces.AccountİntegrationService;
import org.murat.orion.Payment.İnterfaces.PaymentStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.EnableAsync;
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
@EnableAsync
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AccountİntegrationService accountIntegrationService;
    private final Map<PaymentTransactionType, PaymentStrategy> paymentStrategyMap;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentService(AccountİntegrationService accountIntegrationService, PaymentRepository paymentRepository, List< PaymentStrategy> paymentStrategyMap, ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
        Map<PaymentTransactionType, PaymentStrategy> paymentStrategyMap1;
        this.accountIntegrationService = accountIntegrationService;
        this.paymentRepository = paymentRepository;
        paymentStrategyMap1 = paymentStrategyMap1 = paymentStrategyMap.stream()
                .collect(Collectors.toMap(PaymentStrategy::getPaymentType, Function.identity()));
        this.paymentStrategyMap = paymentStrategyMap1;
    }


    @Transactional
    public void process(PaymentRequest paymentRequest) {
        PaymentStrategy paymentStrategy = paymentStrategyMap.get(paymentRequest.getPaymentType());
        if (paymentStrategy == null) {
            throw new IllegalArgumentException("Unsupported payment type: " + paymentRequest.getPaymentType());
        }
        paymentStrategy.processPayment(paymentRequest);
        ProcessCompletedEvent event = new ProcessCompletedEvent(
                paymentRequest.getPaymentType(),
                paymentRequest.getCurrency(),
                paymentRequest.getAmount(),
                paymentRequest.getSourceAccountId(),
                paymentRequest.getTargetAccountId(),
                paymentRequest.getDescription(),
                paymentRequest.getEmail(),
                paymentRequest.getPhoneNumber(),
                "Ödeme İşlemi Tamamlandı"
        );
        applicationEventPublisher.publishEvent(event);

    }
    @Transactional
    public void deposit(long accountId, Double amount, String currency, String email, String phoneNumber) {
        Payment payment =Payment.builder()
                .targetAccountId(accountId)
                .amount(BigDecimal.valueOf(amount))
                .currency(currency)
                .type(PaymentTransactionType.DEPOSIT)
                .status(PaymentTransactionStatus.SUCCESS)
                .referenceCode(UUID.randomUUID().toString())
                .description("Para Yatırma İşlemi")
                .build();
        paymentRepository.save(payment);
        accountIntegrationService.credit(accountId, amount, email, phoneNumber);
        log.info("Deposited payment for account {} with amount {}", accountId, amount);
        DepositCompletedEvent event = new DepositCompletedEvent(
                payment.getType(),
                currency,
                amount,
                accountId,
                email,
                phoneNumber,
                "Para Yatırma İşlemi Tamamlandı"
        );
        applicationEventPublisher.publishEvent(event);
    }
    @Transactional
    public void withdraw(Long accountId, Double amount, String currency, String email, String phoneNumber) {

        Payment transaction = Payment.builder()
                .sourceAccountId(accountId)
                .amount(BigDecimal.valueOf(amount))
                .currency(currency)
                .type(PaymentTransactionType.WITHDRAW)
                .status(PaymentTransactionStatus.SUCCESS)
                .referenceCode(java.util.UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .description("ATM Para Çekme")
                .build();

        paymentRepository.save(transaction);

        accountIntegrationService.debit(accountId, amount, email, phoneNumber);

        log.info("Para çekildi. Account: {}, Tutar: {}", accountId, amount);
        WithDrawComplicatedEvent event = new WithDrawComplicatedEvent(
                currency,
                amount,
                accountId,
                email,
                phoneNumber,
                "Para Çekme İşlemi Tamamlandı"
        );
        applicationEventPublisher.publishEvent(event);
    }
    public Page<PaymentTransferRequest> getAccountHistory(UUID accountId, Pageable pageable) {
        return paymentRepository.findAllByAccountId(accountId, pageable)
                .map(this::mapToDto);
    }

    public Page<PaymentSearchRequest> searchRequests(PaymentSearchRequest request, Pageable pageable) {
        Specification<Payment> spec = PaymentSpecification.getFilteredPayments(request);
        return paymentRepository.findAll(spec, pageable)
                .map(this::MapToDto);
    }

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
    private PaymentSearchRequest MapToDto(Payment entity) {
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
