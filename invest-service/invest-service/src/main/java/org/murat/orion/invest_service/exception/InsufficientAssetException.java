package org.murat.orion.invest_service.exception;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class InsufficientAssetException extends BusinessException {

    public InsufficientAssetException(BigDecimal available, BigDecimal requested) {
        super("Yetersiz varlik miktari. Mevcut=" + available + ", Istenen=" + requested,
                HttpStatus.BAD_REQUEST,
                "INSUFFICIENT_ASSET");
    }
}

