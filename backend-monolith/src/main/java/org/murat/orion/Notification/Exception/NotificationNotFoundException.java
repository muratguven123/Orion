package org.murat.orion.Notification.Exception;

public class NotificationNotFoundException extends NotificationException {

    public NotificationNotFoundException(String message) {
        super(message);
    }

    public NotificationNotFoundException(Long notificationId) {
        super("Notification not found with id: " + notificationId);
    }
}
