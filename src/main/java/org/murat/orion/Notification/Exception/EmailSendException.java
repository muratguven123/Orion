package org.murat.orion.Notification.Exception;

public class EmailSendException extends NotificationException {

    public EmailSendException(String message) {
        super(message);
    }

    public EmailSendException(String email, Throwable cause) {
        super("Failed to send email to: " + email, cause);
    }
}
