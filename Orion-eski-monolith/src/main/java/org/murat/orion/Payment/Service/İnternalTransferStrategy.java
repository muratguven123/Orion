package org.murat.orion.Payment.Service;

import jakarta.transaction.Transaction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.murat.orion.Payment.Dto.Request.PaymentRequest;
import org.murat.orion.Payment.Entity.Payment;
import org.murat.orion.Payment.Entity.PaymentTransactionStatus;
import org.murat.orion.Payment.Entity.PaymentTransactionType;
import org.murat.orion.Payment.Repository.PaymentRepository;
import org.murat.orion.Payment.İnterfaces.AccountİntegrationService;
import org.murat.orion.Payment.İnterfaces.PaymentStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class İnternalTransferStrategy implements PaymentStrategy {
    private final PaymentRepository paymentRepository;
    private final AccountİntegrationService accountIntegrationService;


    public PaymentTransactionType getType() {
        return PaymentTransactionType.TRANSFER_INTERNAL;
    }


    @Transactional
    @Override
    public void processPayment(PaymentRequest request) {
        log.info("İç transfer işlemi başlatıldı. Kaynak: {} -> Hedef: {}", request.getSourceAccountId(), request.getTargetAccountId());
        validateRequest(request);
        String refCode = generateReferenceCode();
        Payment transaction = Payment.builder()
                .amount(BigDecimal.valueOf(request.getAmount()))
                .currency(request.getCurrency())
                .sourceAccountId(request.getSourceAccountId())
                .targetAccountId(request.getTargetAccountId())
                .type(PaymentTransactionType.TRANSFER_INTERNAL)
                .status(PaymentTransactionStatus.PENDING) // İlk durum
                .referenceCode(refCode)
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(transaction);

        try {

            accountIntegrationService.debit(request.getSourceAccountId(), request.getAmount(), request.getEmail(), request.getPhoneNumber());
            accountIntegrationService.credit(request.getTargetAccountId(), request.getAmount(), request.getEmail(), request.getPhoneNumber());
            transaction.setStatus(PaymentTransactionStatus.SUCCESS);
            paymentRepository.save(transaction);
            log.info("Transfer başarıyla tamamlandı. Ref: {}", refCode);
        } catch (Exception e) {
            log.error("Transfer sırasında hata oluştu: {}", e.getMessage());
            transaction.setStatus(PaymentTransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            paymentRepository.save(transaction);
            throw e;
        }
    }
    private void validateRequest(PaymentRequest request) {
        if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
            throw new IllegalArgumentException("Gönderen ve Alıcı hesap aynı olamaz!");
        }
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Transfer tutarı sıfırdan büyük olmalıdır!");
        }
    }

    private String generateReferenceCode() {
        return "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public PaymentTransactionType getPaymentType() {
        return PaymentTransactionType.TRANSFER_INTERNAL;
    }
}
