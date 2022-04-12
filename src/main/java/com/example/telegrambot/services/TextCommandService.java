package com.example.telegrambot.services;

import com.example.telegrambot.entity.BotUsers;
import com.example.telegrambot.repository.BotUsersRepos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextCommandService extends DefaultAbsSender implements TextCommandServiceInterface{

    @Value("${bot.token}")
    String token;


    private final PhotoServiceInterface photoService;
    private final BotUsersRepos botUsersRepos;

    protected TextCommandService(PhotoService getPhoto, BotUsersRepos botUsersRepos) {
        super(new DefaultBotOptions());
        this.photoService = getPhoto;
        this.botUsersRepos = botUsersRepos;
    }

    @Override
    public void getTextMessage(Message message) {
        switch (message.getText()) {
            case ("/start"):
            {
                startBotMessage(message);
                sendCustomKeyboard(message.getChatId().toString());
                break;
            }
            case ("Что нибудь идеальное"):
            {
                sendCatsPhoto(message);
                break;
            }
            default:
                try {
                    execute(new SendMessage(message.getChatId().toString(),"Осуждаю"));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

        }
    }

    @Override
    public String startBotMessage(Message message) {
        BotUsers botUsers=new BotUsers();
        botUsers.setUsername(message.getFrom().getUserName());
        botUsers.setId(message.getFrom().getId());
        botUsersRepos.save(botUsers);
        return "Hello my dear, "+botUsers.getUsername();
    }
    public void sendCustomKeyboard(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Добро пожаловать");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Саня");
        row.add("хуй");
        row.add("соси");
        keyboard.add(row);
        row = new KeyboardRow();
        row.add("Что нибудь идеальное");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendCatsPhoto(Message message) {
        try {
            SendPhoto sendPhoto = new SendPhoto();
            try {
                sendPhoto.setPhoto(photoService.getPhoto());
                sendPhoto.setChatId(message.getChatId().toString());
                execute(sendPhoto);
            } catch (IndexOutOfBoundsException e) {
                execute(new SendMessage(message.getChatId().toString(),"Нет фоток кота"));
            }
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
