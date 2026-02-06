package org.murat.orion.Notification.Exception;

public class SmsSendException extends NotificationException {

    public SmsSendException(String message) {
        super(message);
    }

    public SmsSendException(String phoneNumber, Throwable cause) {
        super("Failed to send SMS to: " + phoneNumber, cause);
    }
}
