package org.murat.orion.InvestDomain.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.murat.orion.InvestDomain.Dto.Request.InvesmentRequest;
import org.murat.orion.InvestDomain.Entity.InvestType;
import org.murat.orion.InvestDomain.Entity.Portfolio;
import org.murat.orion.InvestDomain.Repository.PortfolioRepository;
import org.murat.orion.InvestDomain.Service.InvestService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvestController Unit Tests")
class InvestControllerTest {

    @Mock
    private InvestService investService;

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private InvestController investController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private InvesmentRequest testRequest;
    private Portfolio testPortfolio;

    private static final Long USER_ID = 1L;
    private static final String SYMBOL = "AAPL";
    private static final BigDecimal QUANTITY = new BigDecimal("10");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(investController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        testRequest = InvesmentRequest.builder()
                .userId(USER_ID)
                .symbol(SYMBOL)
                .quantity(QUANTITY)
                .type(InvestType.STOCK)
                .build();

        testPortfolio = Portfolio.builder()
                .id(1L)
                .userId(USER_ID)
                .symbol(SYMBOL)
                .type(InvestType.STOCK)
                .quantity(new BigDecimal("50"))
                .averageCost(new BigDecimal("145.72"))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("buyAsset Tests")
    class BuyAssetTests {

        @Test
        @DisplayName("Should return OK when buy asset is successful")
        void shouldReturnOkWhenBuyAssetIsSuccessful() throws Exception {
            // Given
            doNothing().when(investService).buyAsset(any(InvesmentRequest.class));

            // When & Then
            mockMvc.perform(post("/api/invest/buy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Alım işlemi başarıyla gerçekleşti."));

            verify(investService).buyAsset(any(InvesmentRequest.class));
        }

        @Test
        @DisplayName("Should throw exception when buy asset fails")
        void shouldReturnErrorWhenBuyAssetFails() throws Exception {
            // Given
            doThrow(new RuntimeException("Yetersiz bakiye"))
                    .when(investService).buyAsset(any(InvesmentRequest.class));

            // When & Then
            mockMvc.perform(post("/api/invest/buy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequest)))
                    .andExpect(result -> {
                        Exception exception = result.getResolvedException();
                        assert exception != null;
                        assert exception.getMessage().contains("Yetersiz bakiye");
                    });

            verify(investService).buyAsset(any(InvesmentRequest.class));
        }

        @Test
        @DisplayName("Should return bad request when content type is missing")
        void shouldReturnBadRequestWhenContentTypeIsMissing() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/invest/buy")
                            .content(objectMapper.writeValueAsString(testRequest)))
                    .andExpect(status().isUnsupportedMediaType());

            verify(investService, never()).buyAsset(any());
        }

        @Test
        @DisplayName("Should buy asset with CRYPTO type")
        void shouldBuyAssetWithCryptoType() throws Exception {
            // Given
            InvesmentRequest cryptoRequest = InvesmentRequest.builder()
                    .userId(USER_ID)
                    .symbol("BTC")
                    .quantity(new BigDecimal("0.5"))
                    .type(InvestType.CRYPTO)
                    .build();

            doNothing().when(investService).buyAsset(any(InvesmentRequest.class));

            // When & Then
            mockMvc.perform(post("/api/invest/buy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cryptoRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Alım işlemi başarıyla gerçekleşti."));

            verify(investService).buyAsset(any(InvesmentRequest.class));
        }

        @Test
        @DisplayName("Should buy asset with GOLD type")
        void shouldBuyAssetWithGoldType() throws Exception {
            // Given
            InvesmentRequest goldRequest = InvesmentRequest.builder()
                    .userId(USER_ID)
                    .symbol("XAU")
                    .quantity(new BigDecimal("5"))
                    .type(InvestType.GOLD)
                    .build();

            doNothing().when(investService).buyAsset(any(InvesmentRequest.class));

            // When & Then
            mockMvc.perform(post("/api/invest/buy")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(goldRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Alım işlemi başarıyla gerçekleşti."));

            verify(investService).buyAsset(any(InvesmentRequest.class));
        }
    }

    @Nested
    @DisplayName("sellAsset Tests")
    class SellAssetTests {

        @Test
        @DisplayName("Should return OK when sell asset is successful")
        void shouldReturnOkWhenSellAssetIsSuccessful() throws Exception {
            // Given
            doNothing().when(investService).sellAsset(any(InvesmentRequest.class));

            // When & Then
            mockMvc.perform(post("/api/invest/sell")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequest)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Satış işlemi başarıyla gerçekleşti."));

            verify(investService).sellAsset(any(InvesmentRequest.class));
        }

        @Test
        @DisplayName("Should throw exception when portfolio not found")
        void shouldReturnErrorWhenPortfolioNotFound() throws Exception {
            // Given
            doThrow(new RuntimeException("Portföy bulunamadı."))
                    .when(investService).sellAsset(any(InvesmentRequest.class));

            // When & Then
            mockMvc.perform(post("/api/invest/sell")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequest)))
                    .andExpect(result -> {
                        Exception exception = result.getResolvedException();
                        assert exception != null;
                        assert exception.getMessage().contains("Portföy bulunamadı");
                    });

            verify(investService).sellAsset(any(InvesmentRequest.class));
        }

        @Test
        @DisplayName("Should throw exception when insufficient quantity")
        void shouldReturnErrorWhenInsufficientQuantity() throws Exception {
            // Given
            doThrow(new RuntimeException("Yetersiz varlık miktarı."))
                    .when(investService).sellAsset(any(InvesmentRequest.class));

            // When & Then
            mockMvc.perform(post("/api/invest/sell")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testRequest)))
                    .andExpect(result -> {
                        Exception exception = result.getResolvedException();
                        assert exception != null;
                        assert exception.getMessage().contains("Yetersiz varlık miktarı");
                    });

            verify(investService).sellAsset(any(InvesmentRequest.class));
        }
    }

    @Nested
    @DisplayName("getUserPortfolio Tests")
    class GetUserPortfolioTests {

        @Test
        @DisplayName("Should return portfolio list for user")
        void shouldReturnPortfolioListForUser() throws Exception {
            // Given
            Portfolio portfolio2 = Portfolio.builder()
                    .id(2L)
                    .userId(USER_ID)
                    .symbol("BTC")
                    .type(InvestType.CRYPTO)
                    .quantity(new BigDecimal("2.5"))
                    .averageCost(new BigDecimal("45000.00"))
                    .updatedAt(LocalDateTime.now())
                    .build();

            List<Portfolio> portfolios = Arrays.asList(testPortfolio, portfolio2);
            when(portfolioRepository.findByUserId(USER_ID)).thenReturn(portfolios);

            // When & Then
            mockMvc.perform(get("/api/invest/portfolio/{userId}", USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].symbol").value(SYMBOL))
                    .andExpect(jsonPath("$[0].quantity").value(50))
                    .andExpect(jsonPath("$[1].symbol").value("BTC"))
                    .andExpect(jsonPath("$[1].quantity").value(2.5));

            verify(portfolioRepository).findByUserId(USER_ID);
        }

        @Test
        @DisplayName("Should return empty list when no portfolio exists")
        void shouldReturnEmptyListWhenNoPortfolioExists() throws Exception {
            // Given
            when(portfolioRepository.findByUserId(USER_ID)).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/invest/portfolio/{userId}", USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(portfolioRepository).findByUserId(USER_ID);
        }

        @Test
        @DisplayName("Should return single portfolio item")
        void shouldReturnSinglePortfolioItem() throws Exception {
            // Given
            when(portfolioRepository.findByUserId(USER_ID)).thenReturn(List.of(testPortfolio));

            // When & Then
            mockMvc.perform(get("/api/invest/portfolio/{userId}", USER_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].userId").value(USER_ID))
                    .andExpect(jsonPath("$[0].symbol").value(SYMBOL))
                    .andExpect(jsonPath("$[0].type").value("stock"));

            verify(portfolioRepository).findByUserId(USER_ID);
        }

        @Test
        @DisplayName("Should handle different user IDs")
        void shouldHandleDifferentUserIds() throws Exception {
            // Given
            Long differentUserId = 999L;
            when(portfolioRepository.findByUserId(differentUserId)).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/invest/portfolio/{userId}", differentUserId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(portfolioRepository).findByUserId(differentUserId);
        }
    }
}
