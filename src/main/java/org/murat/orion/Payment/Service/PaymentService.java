package org.murat.orion.Payment.Service;

import lombok.extern.slf4j.Slf4j;
import org.murat.orion.Notification.Events.Payment.DepositCompletedEvent;
import org.murat.orion.Notification.Events.Payment.ProcessCompletedEvent;
import org.murat.orion.Notification.Events.Payment.WithDrawComplicatedEvent;
import org.murat.orion.Payment.Dto.Request.PaymentRequest;
import org.murat.orion.Payment.Dto.Request.PaymentTransferRequest;
import org.murat.orion.Payment.Entity.Payment;
import org.murat.orion.Payment.Entity.PaymentTransactionStatus;
import org.murat.orion.Payment.Entity.PaymentTransactionType;

import org.murat.orion.Payment.Repository.PaymentRepository;
import org.murat.orion.Payment.İnterfaces.AccountİntegrationService;
import org.murat.orion.Payment.İnterfaces.PaymentStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import static jakarta.persistence.GenerationType.UUID;

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
        ProcessCompletedEvent event = new ProcessCompletedEvent(paymentRequest.getPaymentType(),paymentRequest.getCurrency(),paymentRequest.getAmount(),
                paymentRequest.getSourceAccountId(),paymentRequest.getTargetAccountId(),paymentRequest.getDescription());
        applicationEventPublisher.publishEvent(event);

    }
    @Transactional
    public void deposit(long accountId, Double amount,String currency) {
        Payment payment =Payment.builder()
                .targetAccountId(accountId)
                .amount(BigDecimal.valueOf(amount))
                .currency(currency)
                .type(PaymentTransactionType.DEPOSIT)
                .build();
        paymentRepository.save(payment);
        accountIntegrationService.credit(accountId, amount);
        log.info("Deposited payment for account {}", accountId,amount);
        DepositCompletedEvent event = new DepositCompletedEvent(payment.getType(),currency,amount,accountId);
        applicationEventPublisher.publishEvent(event);
    }
    @Transactional
    public void withdraw(Long accountId, Double amount, String currency) {

        Payment transaction = Payment.builder()
                .sourceAccountId(accountId)
                .amount(BigDecimal.valueOf(amount))
                .currency(currency)
                .type(PaymentTransactionType.WITHDRAW)
                .status(PaymentTransactionStatus.SUCCESS)
                .referenceCode(UUID.toString())
                .createdAt(LocalDateTime.now())
                .description("ATM Para Çekme")
                .build();

        paymentRepository.save(transaction);

        accountIntegrationService.debit(accountId, amount.doubleValue());

        log.info("Para çekildi. Account: {}, Tutar: {}", accountId, amount);
        WithDrawComplicatedEvent event = new WithDrawComplicatedEvent(currency,amount,accountId);
        applicationEventPublisher.publishEvent(event);
    }
    public Page<PaymentTransferRequest> getAccountHistory(UUID accountId, Pageable pageable) {
        return paymentRepository.findAllByAccountId(accountId, pageable)
                .map(this::mapToDto);
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
}
