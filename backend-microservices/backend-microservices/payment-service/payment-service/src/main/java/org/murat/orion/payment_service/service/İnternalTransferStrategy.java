package org.murat.orion.payment_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.payment_service.dto.request.BalanceRequest;
import org.murat.orion.payment_service.dto.request.PaymentRequest;
import org.murat.orion.payment_service.entity.OutboxEvent;
import org.murat.orion.payment_service.entity.Payment;
import org.murat.orion.payment_service.entity.PaymentTransactionStatus;
import org.murat.orion.payment_service.entity.PaymentTransactionType;
import org.murat.orion.payment_service.event.PaymentTransferEvent;
import org.murat.orion.payment_service.repository.OutboxEventRepository;
import org.murat.orion.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class İnternalTransferStrategy implements PaymentStrategy {

    private final PaymentRepository paymentRepository;
    private final AccountİntegrationService accountIntegrationService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public PaymentTransactionType getPaymentType() {
        return PaymentTransactionType.TRANSFER_INTERNAL;
    }

    @Transactional
    @Override
    public void processPayment(PaymentRequest request) {
        log.info("İç transfer işlemi başlatıldı. Kaynak: {} -> Hedef: {}", request.getSourceAccountId(), request.getTargetAccountId());
        validateRequest(request);

        String refCode = "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment transaction = Payment.builder()
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .sourceAccountId(request.getSourceAccountId())
                .targetAccountId(request.getTargetAccountId())
                .type(PaymentTransactionType.TRANSFER_INTERNAL)
                .status(PaymentTransactionStatus.PENDING)
                .referenceCode(refCode)
                .description(request.getDescription())
                .build();
        paymentRepository.save(transaction);

        try {
            accountIntegrationService.debit(new BalanceRequest(request.getSourceAccountId(), request.getAmount()));
            accountIntegrationService.credit(new BalanceRequest(request.getTargetAccountId(), request.getAmount()));

            transaction.setStatus(PaymentTransactionStatus.SUCCESS);
            paymentRepository.save(transaction);
            log.info("Transfer başarıyla tamamlandı. Ref: {}", refCode);
        } catch (Exception e) {
            log.error("Transfer sırasında hata oluştu: {}", e.getMessage());
            transaction.setStatus(PaymentTransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            paymentRepository.save(transaction);
            throw new RuntimeException("Transfer işlemi başarısız: " + e.getMessage(), e);
        }

        // Outbox event kaydet
        try {
            PaymentTransferEvent event = new PaymentTransferEvent(
                    request.getSourceAccountId(),
                    request.getTargetAccountId(),
                    request.getAmount(),
                    request.getCurrency(),
                    refCode,
                    request.getDescription(),
                    LocalDateTime.now()
            );
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateType("Payment");
            outboxEvent.setAggregateId(transaction.getId().toString());
            outboxEvent.setEventType("PaymentTransferEvent");
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            outboxEvent.setProcessed(false);
            outboxEventRepository.save(outboxEvent);
            log.info("Payment transfer outbox event kaydedildi: {} -> {}, amount={}",
                    request.getSourceAccountId(), request.getTargetAccountId(), request.getAmount());
        } catch (JsonProcessingException e) {
            log.error("Payment transfer event JSON serialize hatası", e);
        }
    }

    private void validateRequest(PaymentRequest request) {
        if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
            throw new IllegalArgumentException("Gönderen ve Alıcı hesap aynı olamaz!");
        }
        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Transfer tutarı sıfırdan büyük olmalıdır!");
        }
    }

}
