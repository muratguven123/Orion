package org.murat.orion.Notification.Exception;

public class TelegramSendException extends NotificationException {

    public TelegramSendException(String message) {
        super(message);
    }

    public TelegramSendException(String chatId, Throwable cause) {
        super("Failed to send Telegram message to chat: " + chatId, cause);
    }
}
