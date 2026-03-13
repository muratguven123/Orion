package org.murat.orion.invest_service.exception;

import java.time.LocalDateTime;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String errorCode,
        String message,
        String path
) {
}

