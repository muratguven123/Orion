package org.murat.orion.Payment.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.murat.orion.Payment.Dto.Request.PaymentRequest;
import org.murat.orion.Payment.Dto.Request.PaymentTransferRequest;
import org.murat.orion.Payment.Entity.Payment;
import org.murat.orion.Payment.Entity.PaymentTransactionStatus;
import org.murat.orion.Payment.Entity.PaymentTransactionType;
import org.murat.orion.Payment.Repository.PaymentRepository;
import org.murat.orion.Payment.İnterfaces.AccountİntegrationService;
import org.murat.orion.Payment.İnterfaces.PaymentStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountİntegrationService accountIntegrationService;

    @Mock
    private PaymentStrategy internalTransferStrategy;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        when(internalTransferStrategy.getPaymentType()).thenReturn(PaymentTransactionType.TRANSFER_INTERNAL);
        List<PaymentStrategy> strategies = List.of(internalTransferStrategy);
        paymentService = new PaymentService(accountIntegrationService, paymentRepository, strategies);
    }

    @Test
    @DisplayName("process - Geçerli payment type ile işlem başarılı olmalı")
    void process_WithValidPaymentType_ShouldProcessSuccessfully() {
        PaymentRequest request = PaymentRequest.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(100.0)
                .currency("TRY")
                .paymentType(PaymentTransactionType.TRANSFER_INTERNAL)
                .build();

        paymentService.process(request);

        verify(internalTransferStrategy, times(1)).processPayment(request);
    }

    @Test
    @DisplayName("process - Geçersiz payment type ile exception fırlatmalı")
    void process_WithInvalidPaymentType_ShouldThrowException() {
        PaymentRequest request = PaymentRequest.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(100.0)
                .currency("TRY")
                .paymentType(PaymentTransactionType.TRANSFER_EFT)
                .build();

        assertThrows(IllegalArgumentException.class, () -> paymentService.process(request));
    }

    @Test
    @DisplayName("deposit - Para yatırma işlemi başarılı olmalı")
    void deposit_ShouldSavePaymentAndCreditAccount() {
        Long accountId = 1L;
        Double amount = 500.0;
        String currency = "TRY";

        paymentService.deposit(accountId, amount, currency);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(accountId, savedPayment.getTargetAccountId());
        assertEquals(BigDecimal.valueOf(amount), savedPayment.getAmount());
        assertEquals(currency, savedPayment.getCurrency());
        assertEquals(PaymentTransactionType.DEPOSIT, savedPayment.getType());

        verify(accountIntegrationService, times(1)).credit(accountId, amount);
    }

    @Test
    @DisplayName("withdraw - Para çekme işlemi başarılı olmalı")
    void withdraw_ShouldSavePaymentAndDebitAccount() {
        Long accountId = 1L;
        Double amount = 200.0;
        String currency = "TRY";

        paymentService.withdraw(accountId, amount, currency);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();
        assertEquals(accountId, savedPayment.getSourceAccountId());
        assertEquals(BigDecimal.valueOf(amount), savedPayment.getAmount());
        assertEquals(currency, savedPayment.getCurrency());
        assertEquals(PaymentTransactionType.WITHDRAW, savedPayment.getType());
        assertEquals(PaymentTransactionStatus.SUCCESS, savedPayment.getStatus());

        verify(accountIntegrationService, times(1)).debit(accountId, amount);
    }

    @Test
    @DisplayName("getAccountHistory - Hesap geçmişini sayfalı şekilde döndürmeli")
    void getAccountHistory_ShouldReturnPagedHistory() {
        UUID accountId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        Payment payment1 = Payment.builder()
                .id(UUID.randomUUID())
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(BigDecimal.valueOf(100))
                .currency("TRY")
                .type(PaymentTransactionType.TRANSFER_INTERNAL)
                .status(PaymentTransactionStatus.SUCCESS)
                .referenceCode("REF001")
                .description("Test transfer")
                .createdAt(LocalDateTime.now())
                .build();

        Payment payment2 = Payment.builder()
                .id(UUID.randomUUID())
                .targetAccountId(1L)
                .amount(BigDecimal.valueOf(500))
                .currency("TRY")
                .type(PaymentTransactionType.DEPOSIT)
                .status(PaymentTransactionStatus.SUCCESS)
                .referenceCode("REF002")
                .description("ATM Deposit")
                .createdAt(LocalDateTime.now())
                .build();

        Page<Payment> paymentPage = new PageImpl<>(List.of(payment1, payment2), pageable, 2);
        when(paymentRepository.findAllByAccountId(accountId, pageable)).thenReturn(paymentPage);

        Page<PaymentTransferRequest> result = paymentService.getAccountHistory(accountId, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("REF001", result.getContent().get(0).getReferenceCode());
        assertEquals("REF002", result.getContent().get(1).getReferenceCode());
        verify(paymentRepository, times(1)).findAllByAccountId(accountId, pageable);
    }

    @Test
    @DisplayName("getAccountHistory - Boş geçmiş için boş sayfa döndürmeli")
    void getAccountHistory_WithNoHistory_ShouldReturnEmptyPage() {
        UUID accountId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Payment> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(paymentRepository.findAllByAccountId(accountId, pageable)).thenReturn(emptyPage);

        Page<PaymentTransferRequest> result = paymentService.getAccountHistory(accountId, pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("deposit - Sıfır tutar ile de çalışmalı")
    void deposit_WithZeroAmount_ShouldStillProcess() {
        Long accountId = 1L;
        Double amount = 0.0;
        String currency = "TRY";

        paymentService.deposit(accountId, amount, currency);

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(accountIntegrationService, times(1)).credit(accountId, amount);
    }

    @Test
    @DisplayName("withdraw - Farklı para birimi ile çalışmalı")
    void withdraw_WithDifferentCurrency_ShouldProcess() {
        Long accountId = 1L;
        Double amount = 100.0;
        String currency = "USD";

        paymentService.withdraw(accountId, amount, currency);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        assertEquals("USD", paymentCaptor.getValue().getCurrency());
    }
}
