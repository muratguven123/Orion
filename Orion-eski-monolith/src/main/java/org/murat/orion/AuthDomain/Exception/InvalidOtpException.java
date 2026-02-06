package org.murat.orion.AuthDomain.Exception;

public class InvalidOtpException extends AuthException {

    public InvalidOtpException() {
        super("Invalid or expired OTP");
    }

    public InvalidOtpException(String message) {
        super(message);
    }
}
