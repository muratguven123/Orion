package org.murat.orion.payment_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.payment_service.dto.request.BalanceRequest;
import org.murat.orion.payment_service.dto.request.PaymentRequest;
import org.murat.orion.payment_service.dto.request.PaymentSearchRequest;
import org.murat.orion.payment_service.dto.request.PaymentTransferRequest;
import org.murat.orion.payment_service.entity.OutboxEvent;
import org.murat.orion.payment_service.entity.Payment;
import org.murat.orion.payment_service.entity.PaymentTransactionStatus;
import org.murat.orion.payment_service.entity.PaymentTransactionType;
import org.murat.orion.payment_service.event.PaymentDepositEvent;
import org.murat.orion.payment_service.event.PaymentWithdrawEvent;
import org.murat.orion.payment_service.repository.OutboxEventRepository;
import org.murat.orion.payment_service.repository.PaymentRepository;
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
    private final AccountİntegrationService accountIntegrationService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final Map<PaymentTransactionType, PaymentStrategy> paymentStrategyMap;

    public PaymentService(AccountİntegrationService accountIntegrationService,
                          PaymentRepository paymentRepository,
                          OutboxEventRepository outboxEventRepository,
                          ObjectMapper objectMapper,
                          List<PaymentStrategy> paymentStrategies) {
        this.accountIntegrationService = accountIntegrationService;
        this.paymentRepository = paymentRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.paymentStrategyMap = paymentStrategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getPaymentType, Function.identity()));
    }

    @Transactional
    public void deposit(long accountId, Double amount, String currency, String email, String phoneNumber) {
        String refCode = UUID.randomUUID().toString();
        Payment payment = Payment.builder()
                .targetAccountId(accountId)
                .amount(BigDecimal.valueOf(amount))
                .currency(currency)
                .type(PaymentTransactionType.DEPOSIT)
                .status(PaymentTransactionStatus.PENDING)
                .referenceCode(refCode)
                .description("Para Yatırma İşlemi")
                .build();
        paymentRepository.save(payment);

        try {
            accountIntegrationService.credit(new BalanceRequest(accountId, BigDecimal.valueOf(amount)));
            payment.setStatus(PaymentTransactionStatus.SUCCESS);
            paymentRepository.save(payment);
            log.info("Para yatırıldı. Account: {}, Tutar: {}", accountId, amount);
        } catch (Exception e) {
            payment.setStatus(PaymentTransactionStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            log.error("Para yatırma başarısız. Account: {}, Hata: {}", accountId, e.getMessage());
            throw new RuntimeException("Para yatırma işlemi başarısız: " + e.getMessage(), e);
        }

        try {
            PaymentDepositEvent event = new PaymentDepositEvent(
                    accountId, BigDecimal.valueOf(amount), currency, refCode, LocalDateTime.now()
            );
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Payment");
            outboxEvent.setAggregateId(payment.getId().toString());
            outboxEvent.setEventType("PaymentDepositEvent");
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            outboxEvent.setProcessed(false);
            outboxEventRepository.save(outboxEvent);
            log.info("Payment deposit outbox event kaydedildi: accountId={}, amount={}", accountId, amount);
        } catch (JsonProcessingException e) {
            log.error("Payment deposit event JSON serialize hatası", e);
        }
    }

    @Transactional
    public void withdraw(Long accountId, Double amount, String currency, String email, String phoneNumber) {
        String refCode = UUID.randomUUID().toString();
        Payment transaction = Payment.builder()
                .sourceAccountId(accountId)
                .amount(BigDecimal.valueOf(amount))
                .currency(currency)
                .type(PaymentTransactionType.WITHDRAW)
                .status(PaymentTransactionStatus.PENDING)
                .referenceCode(refCode)
                .description("ATM Para Çekme")
                .build();
        paymentRepository.save(transaction);

        try {
            accountIntegrationService.debit(new BalanceRequest(accountId, BigDecimal.valueOf(amount)));
            transaction.setStatus(PaymentTransactionStatus.SUCCESS);
            paymentRepository.save(transaction);
            log.info("Para çekildi. Account: {}, Tutar: {}", accountId, amount);
        } catch (Exception e) {
            transaction.setStatus(PaymentTransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            paymentRepository.save(transaction);
            log.error("Para çekme başarısız. Account: {}, Hata: {}", accountId, e.getMessage());
            throw new RuntimeException("Para çekme işlemi başarısız: " + e.getMessage(), e);
        }

        try {
            PaymentWithdrawEvent event = new PaymentWithdrawEvent(
                    accountId, BigDecimal.valueOf(amount), currency, refCode, LocalDateTime.now()
            );
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Payment");
            outboxEvent.setAggregateId(transaction.getId().toString());
            outboxEvent.setEventType("PaymentWithdrawEvent");
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            outboxEvent.setProcessed(false);
            outboxEventRepository.save(outboxEvent);
            log.info("Payment withdraw outbox event kaydedildi: accountId={}, amount={}", accountId, amount);
        } catch (JsonProcessingException e) {
            log.error("Payment withdraw event JSON serialize hatası", e);
        }
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
