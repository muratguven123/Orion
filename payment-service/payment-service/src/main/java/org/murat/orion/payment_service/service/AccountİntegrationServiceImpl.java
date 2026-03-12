package org.murat.orion.payment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.murat.orion.payment_service.dto.request.BalanceRequest;
import org.murat.orion.payment_service.İnterface.AccountServiceClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountİntegrationServiceImpl implements AccountİntegrationService {

    private final AccountServiceClient accountServiceClient;
    private final OutboxEventRepository outboxEventRepository;

    @Override
    @Retry(name = "accountService", fallbackMethod = "accountServiceFallback")
    @CircuitBreaker(name = "accountService")
    @RateLimiter(name = "accountService")
    @Bulkhead(name = "accountService", type = Bulkhead.Type.SEMAPHORE)
    public void debit(BalanceRequest request) {
        log.info("Bakiye çekiliyor: userId={}, amount={}", request.getUserId(), request.getAmount());
        accountServiceClient.debitBalance(request);
    }

    @Override
    @Retry(name = "accountService", fallbackMethod = "accountServiceFallback")
    @CircuitBreaker(name = "accountService")
    @RateLimiter(name = "accountService")
    @Bulkhead(name = "accountService", type = Bulkhead.Type.SEMAPHORE)
    public void credit(BalanceRequest request) {
        log.info("Bakiye ekleniyor: userId={}, amount={}", request.getUserId(), request.getAmount());
        accountServiceClient.creditBalance(request);
    }

    public void accountServiceFallback(BalanceRequest request, Exception e) {
        log.error("Account-Service'e ulaşılamadı veya limitler aşıldı! Fallback devreye girdi. İşlem iptal ediliyor. UserId: {}, Hata: {}",
                request.getUserId(), e.getMessage());
        try {
                OutboxEvent event = OutboxEvent.builder()
                        .eventType("AccountServiceFailure")
                        .payload(objectMapper.writeValueAsString(request))
                        .build();
                outboxEventRepository.save(event);
                log.info("AccountServiceFailure event'i outbox'a kaydedildi. UserId: {}, EventId: {}", request.getUserId(), event.getId());
        } catch (Exception ex)
        {
            log.error("AccountServiceFailure event'i outbox'a kaydedilemedi! UserId: {}, Hata: {}", request.getUserId(), ex.getMessage(), ex);
        }
        throw new RuntimeException("Hesap servisi şu an yanıt vermiyor, işlem gerçekleştirilemedi. Lütfen daha sonra tekrar deneyiniz.");
    }
}
