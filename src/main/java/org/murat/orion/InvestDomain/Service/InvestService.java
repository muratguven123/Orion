package org.murat.orion.InvestDomain.Service;

import lombok.extern.slf4j.Slf4j;
import org.murat.orion.InvestDomain.Dto.Request.InvesmetnRequest;
import org.murat.orion.InvestDomain.Entity.Invesment;
import org.murat.orion.InvestDomain.Entity.InvestType;
import org.murat.orion.InvestDomain.Entity.Portfolio;
import org.murat.orion.InvestDomain.Mapper.InvestMapper;
import org.murat.orion.InvestDomain.Repository.InvestRepository;
import org.murat.orion.InvestDomain.Repository.PortfolioRepository;
import org.murat.orion.InvestDomain.İnterface.InvesmentStrategy;
import org.murat.orion.InvestDomain.İnterface.InvestAccountİntegrationService;
import org.murat.orion.InvestDomain.İnterface.MarketDataProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InvestService {
    private final PortfolioRepository portfolioRepository;
    private final InvestRepository investRepository;
    private final MarketDataProvider marketDataProvider;
    private final InvestAccountİntegrationService investAccountService;
    private final Map<InvestType, InvesmentStrategy> investStrategyMap;
    private final InvestMapper investMapper;

    public InvestService(PortfolioRepository portfolioRepository,
                         InvestRepository investRepository,
                         MarketDataProvider marketDataProvider,
                         InvestAccountİntegrationService investAccountService,
                         List<InvesmentStrategy> investStrategies,
                         InvestMapper investMapper) {
        this.portfolioRepository = portfolioRepository;
        this.investRepository = investRepository;
        this.marketDataProvider = marketDataProvider;
        this.investAccountService = investAccountService;
        this.investStrategyMap = investStrategies.stream()
                .collect(Collectors.toMap(InvesmentStrategy::getInvestType, Function.identity()));
        this.investMapper = investMapper;
    }
    @Transactional
    public void buyAsset(InvesmetnRequest request){
        InvesmentStrategy strategy = investStrategyMap.get(request.getType());
        if (strategy == null) {
            log.error("Yatırım stratejisi bulunamadı: {}", request.getType());
            throw new RuntimeException("Yatırım stratejisi bulunamadı: " + request.getType());
        }
        strategy.validExecute(request.getSymbol());

        BigDecimal currentPrice = marketDataProvider.getCurrentPrice(request.getSymbol());
        BigDecimal totalCost = currentPrice.multiply(request.getQuantity());
        log.info("ALIM EMRİ: {} adet {} @ {} - Toplam: {}",
                request.getQuantity(), request.getSymbol(), currentPrice, totalCost);

        investAccountService.debitBalance(request.getUserId(), totalCost);

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

        Invesment invesment = investMapper.toEntity(request, currentPrice, totalCost);
        investRepository.save(invesment);
        log.info("Yatırım Başarılı! Yeni Portföy Adedi: {}", newTotalQuantity);
    }
    @Transactional
    public void sellAsset(InvesmetnRequest request){
        InvesmentStrategy strategy = investStrategyMap.get(request.getType());
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

        investAccountService.creditBalance(request.getUserId(), totalProceeds);

        Invesment invesment = investMapper.toEntity(request, currentPrice, totalProceeds);
        investRepository.save(invesment);
        log.info("Satış Başarılı! Kalan Portföy Adedi: {}", portfolio.getQuantity());
    }
}
