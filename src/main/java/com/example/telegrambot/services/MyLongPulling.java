package com.example.telegrambot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

@Component
public class MyLongPulling extends TelegramLongPollingBot {


    @Value("${name}")
    private String botName;

    @Value("${token}")
    private String token;

    private final BotCommandsServiceInterface botCommandsServiceInterface;

    public MyLongPulling(BotCommandsService botCommandsServiceInterface) {
        this.botCommandsServiceInterface = botCommandsServiceInterface;
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
    }

    @Override
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
            try {
                File file=new File("img/"+update.getMessage().getPhoto().get(2).getFileId()+".jpg");
                if(file.createNewFile()) {
                    String url=getBaseUrl()+"getFile?file_id="+update.getMessage().getPhoto().get(2).getFileId();
                    downloadFile(botCommandsServiceInterface.getImagePath(url), file);
                    message.setText(botCommandsServiceInterface.saveImage(update,file));
                }
                else {
                    message.setText("Не судьба");
                }
            } catch (NoSuchElementException e) {
                message.setText("Наберите /start для начала");
            } catch (TelegramApiException | IOException e) {
                e.printStackTrace();
            }
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
