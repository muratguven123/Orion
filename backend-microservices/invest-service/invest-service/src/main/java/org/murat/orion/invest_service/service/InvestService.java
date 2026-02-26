package org.murat.orion.invest_service.service;

import org.murat.orion.invest_service.dto.request.InvesmentRequest;
import org.murat.orion.invest_service.dto.request.BalanceRequest;
import org.murat.orion.invest_service.entity.InvestType;
import org.murat.orion.invest_service.entity.Investment;
import org.murat.orion.invest_service.entity.Portfolio;
import org.murat.orion.invest_service.mapper.InvestMapper;
import org.murat.orion.invest_service.repository.InvestRepository;
import org.murat.orion.invest_service.repository.PortfolioRepository;
import org.murat.orion.invest_service.interfaces.InvestmentStrategy;
import org.murat.orion.invest_service.interfaces.InvestAccountIntegrationService;
import org.murat.orion.invest_service.interfaces.MarketDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InvestService {

    private static final Logger log = LoggerFactory.getLogger(InvestService.class);
    private final PortfolioRepository portfolioRepository;
    private final InvestRepository investRepository;
    private final MarketDataProvider marketDataProvider;
    private final InvestAccountIntegrationService investAccountService;
    private final Map<InvestType, InvestmentStrategy> investStrategyMap;
    private final InvestMapper investMapper;

    public InvestService(PortfolioRepository portfolioRepository,
                         InvestRepository investRepository,
                         MarketDataProvider marketDataProvider,
                         InvestAccountIntegrationService investAccountService,
                         List<InvestmentStrategy> investStrategies,
                         InvestMapper investMapper) {
        this.portfolioRepository = portfolioRepository;
        this.investRepository = investRepository;
        this.marketDataProvider = marketDataProvider;
        this.investAccountService = investAccountService;
        this.investStrategyMap = investStrategies.stream()
                .collect(Collectors.toMap(InvestmentStrategy::getInvestType, Function.identity()));
        this.investMapper = investMapper;
    }

    @Transactional
    public void buyAsset(InvesmentRequest request) {
        InvestmentStrategy strategy = investStrategyMap.get(request.getType());
        if (strategy == null) {
            log.error("Yatırım stratejisi bulunamadı: {}", request.getType());
            throw new RuntimeException("Yatırım stratejisi bulunamadı: " + request.getType());
        }
        strategy.validExecute(request.getSymbol());

        BigDecimal currentPrice = marketDataProvider.getCurrentPrice(request.getSymbol());
        BigDecimal totalCost = currentPrice.multiply(request.getQuantity());
        log.info("ALIM EMRİ: {} adet {} @ {} - Toplam: {}",
                request.getQuantity(), request.getSymbol(), currentPrice, totalCost);

        investAccountService.debitBalance(new BalanceRequest(request.getUserId(), totalCost));

        Portfolio portfolio = portfolioRepository.findByUserIdAndSymbol(request.getUserId(), request.getSymbol())
                .orElseGet(() -> Portfolio.builder()
                        .userId(request.getUserId())
                        .symbol(request.getSymbol())
                        .type(request.getType())
                        .quantity(BigDecimal.ZERO)
                        .averageCost(BigDecimal.ZERO)
                        .build());
        BigDecimal currentTotalValue = portfolio.getQuantity().multiply(portfolio.getAverageCost());
        BigDecimal newTotalValue = currentTotalValue.add(totalCost);
        BigDecimal newTotalQuantity = portfolio.getQuantity().add(request.getQuantity());
        BigDecimal newAverageCost = newTotalValue.divide(newTotalQuantity, 4, RoundingMode.HALF_UP);
        portfolio.setQuantity(newTotalQuantity);
        portfolio.setAverageCost(newAverageCost);
        portfolioRepository.save(portfolio);

        Investment investment = investMapper.toEntity(request, currentPrice, totalCost);
        investRepository.save(investment);
        log.info("Yatırım Başarılı! Yeni Portföy Adedi: {}", newTotalQuantity);
    }

    @Transactional
    public void sellAsset(InvesmentRequest request) {
        InvestmentStrategy strategy = investStrategyMap.get(request.getType());
        if (strategy == null) {
            log.error("Yatırım stratejisi bulunamadı: {}", request.getType());
            throw new RuntimeException("Yatırım stratejisi bulunamadı: " + request.getType());
        }
        strategy.validExecute(request.getSymbol());

        Portfolio portfolio = portfolioRepository.findByUserIdAndSymbol(request.getUserId(), request.getSymbol())
                .orElseThrow(() -> new RuntimeException("Portföy bulunamadı."));

        if (portfolio.getQuantity().compareTo(request.getQuantity()) < 0) {
            log.error("Yetersiz varlık miktarı. Mevcut: {}, İstenen: {}",
                    portfolio.getQuantity(), request.getQuantity());
            throw new RuntimeException("Yetersiz varlık miktarı.");
        }

        BigDecimal currentPrice = marketDataProvider.getCurrentPrice(request.getSymbol());
        BigDecimal totalProceeds = currentPrice.multiply(request.getQuantity());
        log.info("SATIM EMRİ: {} adet {} @ {} - Toplam Gelir: {}",
                request.getQuantity(), request.getSymbol(), currentPrice, totalProceeds);

        portfolio.setQuantity(portfolio.getQuantity().subtract(request.getQuantity()));
        portfolioRepository.save(portfolio);

        investAccountService.creditBalance(new BalanceRequest(request.getUserId(), totalProceeds));

        Investment investment = investMapper.toEntity(request, currentPrice, totalProceeds);
        investRepository.save(investment);
        log.info("Satış Başarılı! Kalan Portföy Adedi: {}", portfolio.getQuantity());
    }
}
