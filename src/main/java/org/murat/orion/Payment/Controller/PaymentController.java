package org.murat.orion.Payment.Controller;

import lombok.RequiredArgsConstructor;
import org.murat.orion.Payment.Dto.Request.BalanceOperationRequest;
import org.murat.orion.Payment.Dto.Request.PaymentRequest;
import org.murat.orion.Payment.Dto.Request.PaymentTransferRequest;
import org.murat.orion.Payment.Service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody PaymentRequest request) {

        paymentService.process(request);

        return ResponseEntity.ok("Transfer işlemi başarıyla alındı.");
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody BalanceOperationRequest request) {
        paymentService.deposit(request.getAccountId(), request.getAmount(), request.getCurrency());
        return ResponseEntity.ok("Para yatırma işlemi başarılı.");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody BalanceOperationRequest request) {
        paymentService.withdraw(request.getAccountId(), request.getAmount(), request.getCurrency());
        return ResponseEntity.ok("Para çekme işlemi başarılı.");
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<Page<PaymentTransferRequest>> getHistory(
            @PathVariable UUID accountId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(paymentService.getAccountHistory(accountId, pageable));
    }
}
