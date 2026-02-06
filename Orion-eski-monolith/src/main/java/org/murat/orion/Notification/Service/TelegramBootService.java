package org.murat.orion.Notification.Service;

import org.murat.orion.Notification.İnterface.smsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@EnableAsync
public class TelegramBootService extends TelegramLongPollingBot implements smsProvider {

    private final String botUsername;

    public TelegramBootService(@Value("${telegram.bot.token}") String botToken,
                               @Value("${telegram.bot.username}") String botUsername) {
        super(botToken);
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
    }

    @Override
    @Async
    public void sendSms(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean supports(String provider) {
        return "TELEGRAM".equalsIgnoreCase(provider);
    }
}
