package org.murat.orion.payment_service.service;

import org.murat.orion.payment_service.dto.request.BalanceRequest;

public interface AccountİntegrationService {
    void debit(BalanceRequest request);
    void credit(BalanceRequest request);
}
