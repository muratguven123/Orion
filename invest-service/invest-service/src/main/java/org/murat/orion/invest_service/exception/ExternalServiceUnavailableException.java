package org.murat.orion.invest_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


public class ExternalServiceUnavailableException extends BusinessException {

    public ExternalServiceUnavailableException(String serviceName) {
        super(serviceName + " su anda hizmet veremiyor. Lutfen daha sonra tekrar deneyin.",
                HttpStatus.SERVICE_UNAVAILABLE,
                "EXTERNAL_SERVICE_UNAVAILABLE");
    }
}
