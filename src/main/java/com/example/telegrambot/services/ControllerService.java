package com.example.telegrambot.services;

import com.example.telegrambot.repository.BotUsersRepos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class ControllerService extends DefaultAbsSender implements ControllerServiceInterface{

    public static Logger logger= LoggerFactory.getLogger(ControllerService.class);

    @Value("${bot.token}")
    private String token;

    private final BotUsersRepos botUsersRepos;

    protected ControllerService(BotUsersRepos botUsersRepos) {
        super(new DefaultBotOptions());
        this.botUsersRepos = botUsersRepos;
    }

    @Override
    public void sendMesForAllUser(String message){
        botUsersRepos.findAll().forEach(botUsers -> {
            try {
                execute(new SendMessage(botUsers.getId().toString(),message));
            } catch (TelegramApiException e) {
                logger.error("Cant send message to user"+e.getMessage());
            }
        });
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
