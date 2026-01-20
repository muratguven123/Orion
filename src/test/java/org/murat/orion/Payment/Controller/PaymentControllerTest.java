package org.murat.orion.Payment.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.murat.orion.Payment.Dto.Request.BalanceOperationRequest;
import org.murat.orion.Payment.Dto.Request.PaymentRequest;
import org.murat.orion.Payment.Dto.Request.PaymentTransferRequest;
import org.murat.orion.Payment.Entity.PaymentTransactionStatus;
import org.murat.orion.Payment.Entity.PaymentTransactionType;
import org.murat.orion.Payment.Service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        objectMapper = new ObjectMapper();
    }
    @Test
    @DisplayName("POST /transfer - Transfer başarılı")
    void transfer_Success() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(100.0)
                .currency("TRY")
                .paymentType(PaymentTransactionType.TRANSFER_INTERNAL)
                .build();

        mockMvc.perform(post("/api/payments/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer işlemi başarıyla alındı."));

        verify(paymentService, times(1)).process(any(PaymentRequest.class));
    }

    @Test
    @DisplayName("POST /deposit - Para yatırma başarılı")
    void deposit_Success() throws Exception {
        BalanceOperationRequest request = new BalanceOperationRequest();
        request.setAccountId(1L);
        request.setAmount(500.0);
        request.setCurrency("TRY");

        mockMvc.perform(post("/api/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Para yatırma işlemi başarılı."));

        verify(paymentService, times(1)).deposit(1L, 500.0, "TRY");
    }

    @Test
    @DisplayName("POST /withdraw - Para çekme başarılı")
    void withdraw_Success() throws Exception {
        BalanceOperationRequest request = new BalanceOperationRequest();
        request.setAccountId(1L);
        request.setAmount(200.0);
        request.setCurrency("TRY");

        mockMvc.perform(post("/api/payments/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Para çekme işlemi başarılı."));

        verify(paymentService, times(1)).withdraw(1L, 200.0, "TRY");
    }

    @Test
    @DisplayName("GET /history - Hesap geçmişi döndürmeli")
    void getHistory_Success() throws Exception {
        UUID accountId = UUID.randomUUID();

        PaymentTransferRequest transfer = PaymentTransferRequest.builder()
                .referenceCode("REF001")
                .sourceAccount("1")
                .targetAccount("2")
                .amount(100.0)
                .currency("TRY")
                .type(PaymentTransactionType.TRANSFER_INTERNAL)
                .status(PaymentTransactionStatus.SUCCESS)
                .date(LocalDateTime.now())
                .build();

        Page<PaymentTransferRequest> page = new PageImpl<>(List.of(transfer), PageRequest.of(0, 10), 1);
        when(paymentService.getAccountHistory(eq(accountId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/payments/history/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].referenceCode").value("REF001"));

        verify(paymentService, times(1)).getAccountHistory(eq(accountId), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /history - Boş geçmiş")
    void getHistory_Empty() throws Exception {
        UUID accountId = UUID.randomUUID();
        Page<PaymentTransferRequest> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(paymentService.getAccountHistory(eq(accountId), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/payments/history/" + accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("POST /deposit - USD para birimi")
    void deposit_WithUSD() throws Exception {
        BalanceOperationRequest request = new BalanceOperationRequest();
        request.setAccountId(1L);
        request.setAmount(100.0);
        request.setCurrency("USD");

        mockMvc.perform(post("/api/payments/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(paymentService, times(1)).deposit(1L, 100.0, "USD");
    }

    @Test
    @DisplayName("POST /withdraw - Büyük tutar")
    void withdraw_LargeAmount() throws Exception {
        BalanceOperationRequest request = new BalanceOperationRequest();
        request.setAccountId(1L);
        request.setAmount(1000000.0);
        request.setCurrency("TRY");

        mockMvc.perform(post("/api/payments/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(paymentService, times(1)).withdraw(1L, 1000000.0, "TRY");
    }

    @Test
    @DisplayName("POST /transfer - EFT transfer")
    void transfer_EFT() throws Exception {
        PaymentRequest request = PaymentRequest.builder()
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .amount(250.0)
                .currency("TRY")
                .paymentType(PaymentTransactionType.TRANSFER_EFT)
                .build();

        mockMvc.perform(post("/api/payments/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(paymentService, times(1)).process(any(PaymentRequest.class));
    }
}
