package org.murat.orion.InvestDomain.Controller;

import lombok.RequiredArgsConstructor;
import org.murat.orion.InvestDomain.Dto.Request.InvesmentRequest;
import org.murat.orion.InvestDomain.Entity.Portfolio;
import org.murat.orion.InvestDomain.Repository.PortfolioRepository;
import org.murat.orion.InvestDomain.Service.InvestService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/invest")
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
        return ResponseEntity.ok("Satış işlemi başarıyla gerçekleşti.");
    }

    @GetMapping("/portfolio/{userId}")
    public ResponseEntity<List<Portfolio>> getUserPortfolio(@PathVariable Long userId) {
        List<Portfolio> assets = portfolioRepository.findByUserId(userId);
        return ResponseEntity.ok(assets);
    }
}