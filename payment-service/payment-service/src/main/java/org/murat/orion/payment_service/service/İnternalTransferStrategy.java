package org.murat.orion.payment_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.payment_service.dto.request.PaymentRequest;
import org.murat.orion.payment_service.entity.Payment;
import org.murat.orion.payment_service.entity.PaymentTransactionStatus;
import org.murat.orion.payment_service.entity.PaymentTransactionType;
import org.murat.orion.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class İnternalTransferStrategy  implements  PaymentStrategy{
    private final PaymentRepository paymentRepository;
//    private final AccountİntegrationService accountIntegrationService;


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
//                .amount(BigDecimal.valueOf(request.getAmount()))
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
//        if (request.getAmount() <= 0) {
//            throw new IllegalArgumentException("Transfer tutarı sıfırdan büyük olmalıdır!");
//        }
    }

    private String generateReferenceCode() {
        return "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public PaymentTransactionType getPaymentType() {
        return PaymentTransactionType.TRANSFER_INTERNAL;
    }

}
