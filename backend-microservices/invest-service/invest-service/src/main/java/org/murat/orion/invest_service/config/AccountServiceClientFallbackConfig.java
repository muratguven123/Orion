package org.murat.orion.invest_service.config;

import org.murat.orion.invest_service.interfaces.AccountServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.math.BigDecimal;

@Configuration
public class AccountServiceClientFallbackConfig {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceClientFallbackConfig.class);

    @Bean
    @ConditionalOnMissingBean(AccountServiceClient.class)
    public AccountServiceClient accountServiceClientFallback() {
           return new AccountServiceClient() {
            @Override
            public void debitBalance(Long userId, BigDecimal amount) {
                log.warn("Fallback debitBalance called for userId={} amount={}. No account-service available.", userId, amount);
            }

            @Override
            public void creditBalance(Long userId, BigDecimal amount) {
                log.warn("Fallback creditBalance called for userId={} amount={}. No account-service available.", userId, amount);
            }
        };
    }
}
