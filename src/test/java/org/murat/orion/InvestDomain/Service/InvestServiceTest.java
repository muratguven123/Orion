package org.murat.orion.InvestDomain.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.murat.orion.InvestDomain.Dto.Request.InvesmentRequest;
import org.murat.orion.InvestDomain.Entity.Invesment;
import org.murat.orion.InvestDomain.Entity.InvestType;
import org.murat.orion.InvestDomain.Entity.Portfolio;
import org.murat.orion.InvestDomain.Mapper.InvestMapper;
import org.murat.orion.InvestDomain.Repository.InvestRepository;
import org.murat.orion.InvestDomain.Repository.PortfolioRepository;
import org.murat.orion.InvestDomain.İnterface.InvesmentStrategy;
import org.murat.orion.InvestDomain.İnterface.InvestAccountİntegrationService;
import org.murat.orion.InvestDomain.İnterface.MarketDataProvider;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvestService Unit Tests")
class InvestServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private InvestRepository investRepository;

    @Mock
    private MarketDataProvider marketDataProvider;

    @Mock
    private InvestAccountİntegrationService investAccountService;

    @Mock
    private InvestMapper investMapper;

    @Mock
    private InvesmentStrategy stockStrategy;

    private InvestService investService;

    private InvesmentRequest testRequest;
    private Portfolio testPortfolio;
    private Invesment testInvesment;

    private static final Long USER_ID = 1L;
    private static final String SYMBOL = "AAPL";
    private static final BigDecimal QUANTITY = new BigDecimal("10");
    private static final BigDecimal CURRENT_PRICE = new BigDecimal("150.00");
    private static final BigDecimal TOTAL_COST = new BigDecimal("1500.00");

    @BeforeEach
    void setUp() {
        when(stockStrategy.getInvestType()).thenReturn(InvestType.STOCK);

        investService = new InvestService(
                portfolioRepository,
                investRepository,
                marketDataProvider,
                investAccountService,
                List.of(stockStrategy),
                investMapper
        );

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
                .averageCost(new BigDecimal("140.00"))
                .build();

        testInvesment = Invesment.builder()
                .id(1L)
                .userId(USER_ID)
                .symbol(SYMBOL)
                .i̇nvestType(InvestType.STOCK)
                .quantity(QUANTITY)
                .price(CURRENT_PRICE)
                .amount(TOTAL_COST)
                .build();
    }

    @Nested
    @DisplayName("buyAsset Tests")
    class BuyAssetTests {

        @Test
        @DisplayName("Should successfully buy asset when portfolio exists")
        void shouldSuccessfullyBuyAssetWhenPortfolioExists() {
            // Given
            doNothing().when(stockStrategy).validExecute(SYMBOL);
            when(marketDataProvider.getCurrentPrice(SYMBOL)).thenReturn(CURRENT_PRICE);
            doNothing().when(investAccountService).debitBalance(USER_ID, TOTAL_COST);
            when(portfolioRepository.findByUserIdAndSymbol(USER_ID, SYMBOL)).thenReturn(Optional.of(testPortfolio));
            when(portfolioRepository.save(any(Portfolio.class))).thenReturn(testPortfolio);
            when(investMapper.toEntity(any(InvesmentRequest.class), any(BigDecimal.class), any(BigDecimal.class)))
                    .thenReturn(testInvesment);
            when(investRepository.save(any(Invesment.class))).thenReturn(testInvesment);

            // When
            investService.buyAsset(testRequest);

            // Then
            verify(stockStrategy).validExecute(SYMBOL);
            verify(marketDataProvider).getCurrentPrice(SYMBOL);
            verify(investAccountService).debitBalance(USER_ID, TOTAL_COST);
            verify(portfolioRepository).findByUserIdAndSymbol(USER_ID, SYMBOL);
            verify(portfolioRepository).save(any(Portfolio.class));
            verify(investRepository).save(any(Invesment.class));
        }

        @Test
        @DisplayName("Should successfully buy asset when portfolio does not exist")
        void shouldSuccessfullyBuyAssetWhenPortfolioDoesNotExist() {
            // Given
            doNothing().when(stockStrategy).validExecute(SYMBOL);
            when(marketDataProvider.getCurrentPrice(SYMBOL)).thenReturn(CURRENT_PRICE);
            doNothing().when(investAccountService).debitBalance(USER_ID, TOTAL_COST);
            when(portfolioRepository.findByUserIdAndSymbol(USER_ID, SYMBOL)).thenReturn(Optional.empty());
            when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(investMapper.toEntity(any(InvesmentRequest.class), any(BigDecimal.class), any(BigDecimal.class)))
                    .thenReturn(testInvesment);
            when(investRepository.save(any(Invesment.class))).thenReturn(testInvesment);

            // When
            investService.buyAsset(testRequest);

            // Then
            verify(portfolioRepository).findByUserIdAndSymbol(USER_ID, SYMBOL);
            verify(portfolioRepository).save(any(Portfolio.class));
            verify(investRepository).save(any(Invesment.class));
        }

        @Test
        @DisplayName("Should throw exception when strategy not found")
        void shouldThrowExceptionWhenStrategyNotFound() {
            // Given
            InvesmentRequest invalidRequest = InvesmentRequest.builder()
                    .userId(USER_ID)
                    .symbol(SYMBOL)
                    .quantity(QUANTITY)
                    .type(InvestType.CRYPTO) // No strategy registered for CRYPTO
                    .build();

            // When & Then
            assertThatThrownBy(() -> investService.buyAsset(invalidRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Yatırım stratejisi bulunamadı");
        }

        @Test
        @DisplayName("Should throw exception when debit balance fails")
        void shouldThrowExceptionWhenDebitBalanceFails() {
            // Given
            doNothing().when(stockStrategy).validExecute(SYMBOL);
            when(marketDataProvider.getCurrentPrice(SYMBOL)).thenReturn(CURRENT_PRICE);
            doThrow(new RuntimeException("Yetersiz bakiye"))
                    .when(investAccountService).debitBalance(USER_ID, TOTAL_COST);

            // When & Then
            assertThatThrownBy(() -> investService.buyAsset(testRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Yetersiz bakiye");

            verify(portfolioRepository, never()).save(any());
            verify(investRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("sellAsset Tests")
    class SellAssetTests {

        @Test
        @DisplayName("Should successfully sell asset")
        void shouldSuccessfullySellAsset() {
            // Given
            doNothing().when(stockStrategy).validExecute(SYMBOL);
            when(portfolioRepository.findByUserIdAndSymbol(USER_ID, SYMBOL)).thenReturn(Optional.of(testPortfolio));
            when(marketDataProvider.getCurrentPrice(SYMBOL)).thenReturn(CURRENT_PRICE);
            when(portfolioRepository.save(any(Portfolio.class))).thenReturn(testPortfolio);
            doNothing().when(investAccountService).creditBalance(eq(USER_ID), any(BigDecimal.class));
            when(investMapper.toEntity(any(InvesmentRequest.class), any(BigDecimal.class), any(BigDecimal.class)))
                    .thenReturn(testInvesment);
            when(investRepository.save(any(Invesment.class))).thenReturn(testInvesment);

            // When
            investService.sellAsset(testRequest);

            // Then
            verify(stockStrategy).validExecute(SYMBOL);
            verify(portfolioRepository).findByUserIdAndSymbol(USER_ID, SYMBOL);
            verify(marketDataProvider).getCurrentPrice(SYMBOL);
            verify(portfolioRepository).save(any(Portfolio.class));
            verify(investAccountService).creditBalance(eq(USER_ID), any(BigDecimal.class));
            verify(investRepository).save(any(Invesment.class));
        }

        @Test
        @DisplayName("Should throw exception when portfolio not found")
        void shouldThrowExceptionWhenPortfolioNotFound() {
            // Given
            doNothing().when(stockStrategy).validExecute(SYMBOL);
            when(portfolioRepository.findByUserIdAndSymbol(USER_ID, SYMBOL)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> investService.sellAsset(testRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Portföy bulunamadı");

            verify(investAccountService, never()).creditBalance(any(), any());
            verify(investRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when insufficient quantity")
        void shouldThrowExceptionWhenInsufficientQuantity() {
            // Given
            Portfolio lowQuantityPortfolio = Portfolio.builder()
                    .id(1L)
                    .userId(USER_ID)
                    .symbol(SYMBOL)
                    .type(InvestType.STOCK)
                    .quantity(new BigDecimal("5")) // Less than requested 10
                    .averageCost(new BigDecimal("140.00"))
                    .build();

            doNothing().when(stockStrategy).validExecute(SYMBOL);
            when(portfolioRepository.findByUserIdAndSymbol(USER_ID, SYMBOL)).thenReturn(Optional.of(lowQuantityPortfolio));

            // When & Then
            assertThatThrownBy(() -> investService.sellAsset(testRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Yetersiz varlık miktarı");

            verify(investAccountService, never()).creditBalance(any(), any());
            verify(investRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when strategy not found for sell")
        void shouldThrowExceptionWhenStrategyNotFoundForSell() {
            // Given
            InvesmentRequest invalidRequest = InvesmentRequest.builder()
                    .userId(USER_ID)
                    .symbol(SYMBOL)
                    .quantity(QUANTITY)
                    .type(InvestType.GOLD) // No strategy registered for GOLD
                    .build();

            // When & Then
            assertThatThrownBy(() -> investService.sellAsset(invalidRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Yatırım stratejisi bulunamadı");
        }
    }
}
