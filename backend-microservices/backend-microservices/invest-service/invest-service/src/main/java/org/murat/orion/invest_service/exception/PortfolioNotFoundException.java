package org.murat.orion.invest_service.exception;

import org.springframework.http.HttpStatus;

public class PortfolioNotFoundException extends BusinessException {

    public PortfolioNotFoundException(Long userId, String symbol) {
        super("Portfoy bulunamadi. userId=" + userId + ", symbol=" + symbol, HttpStatus.NOT_FOUND, "PORTFOLIO_NOT_FOUND");
    }
}

