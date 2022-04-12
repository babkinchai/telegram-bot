package com.example.telegrambot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class MyLongPulling extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String token;

    private final TextCommandServiceInterface textCommandServiceInterface;


    private final PhotoServiceInterface photoServiceInterface;

    public MyLongPulling(TextCommandService textCommandServiceInterface,
                         PhotoService photoServiceInterface
    ) {
        this.textCommandServiceInterface = textCommandServiceInterface;
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
        if (update.getMessage().hasText()) {
            textCommandServiceInterface.getTextMessage(update.getMessage());
        }
        else if (update.getMessage().hasPhoto()) {
            try {
                photoServiceInterface.savePhoto(update.getMessage());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

/*    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        if(update.getMessage().hasText()){
            if ("/start".equals(update.getMessage().getText())) {
                message.setText(botCommandsServiceInterface.startBotMessage(update));
            } else {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(botCommandsServiceInterface.getPhoto());
                sendPhoto.setChatId(update.getMessage().getChatId().toString());
                try {
                    execute(sendPhoto);
                    message.setText("Кот");
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        if(update.getMessage().hasPhoto()){

        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }*/
}
