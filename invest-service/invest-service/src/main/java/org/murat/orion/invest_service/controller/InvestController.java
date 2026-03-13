package org.murat.orion.invest_service.controller;

import lombok.RequiredArgsConstructor;
import org.murat.orion.invest_service.dto.request.InvesmentRequest;

import org.murat.orion.invest_service.entity.Portfolio;
import org.murat.orion.invest_service.repository.PortfolioRepository;
import org.murat.orion.invest_service.service.InvestService;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/invest")
@EnableFeignClients
public class InvestController {

    private final InvestService investService;
    private final PortfolioRepository portfolioRepository;


    @PostMapping(value = "/buy", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> buyAsset(@RequestBody InvesmentRequest request) {
        investService.buyAsset(request);
        return ResponseEntity.ok("Alım işlemi başarıyla gerçekleşti.");
    }

    @PostMapping(value = "/sell", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sellAsset(@RequestBody InvesmentRequest request) {
        investService.sellAsset(request);
        return ResponseEntity.ok("Satım işlemi başarıyla gerçekleşti.");
    }

    @GetMapping("/portfolio/{userId}")
    public ResponseEntity<List<Portfolio>> getUserPortfolio(@PathVariable Long userId) {
        List<Portfolio> assets = portfolioRepository.findByUserId(userId);
        return ResponseEntity.ok(assets);
    }
}