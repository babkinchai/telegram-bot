package com.example.telegrambot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MyLongPulling extends TelegramLongPollingBot {

    public static Logger logger= LoggerFactory.getLogger(MyLongPulling.class);

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String token;

    private final TextCommandServiceInterface textCommandServiceInterface;

    private CallbackServiceInterface callbackServiceInterface;

    private final PhotoServiceInterface photoServiceInterface;

    public MyLongPulling(TextCommandService textCommandServiceInterface,
                         CallbackService callbackServiceInterface,
                         PhotoService photoServiceInterface) {
        this.textCommandServiceInterface = textCommandServiceInterface;

        this.callbackServiceInterface = callbackServiceInterface;
        this.photoServiceInterface = photoServiceInterface;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onRegister() {
        System.out.println("hello");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasPhoto()) {
                try {
                    photoServiceInterface.savePhoto(update.getMessage());
                } catch (TelegramApiException e) {
                    logger.error("SavePhoto method error. Cant send message for client. " + e.getMessage());
                }
            }else
                textCommandServiceInterface.getTextMessage(update.getMessage());

        }else if (update.hasCallbackQuery()) {
            callbackServiceInterface.getCallback(update.getCallbackQuery());
        }
    }
}


