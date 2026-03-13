package org.murat.orion.invest_service.exception;

import org.springframework.http.HttpStatus;

public class StrategyNotFoundException extends BusinessException {

    public StrategyNotFoundException(String type) {
        super("Yatirim stratejisi bulunamadi: " + type, HttpStatus.BAD_REQUEST, "STRATEGY_NOT_FOUND");
    }
}

