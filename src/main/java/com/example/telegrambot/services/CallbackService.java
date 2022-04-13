package com.example.telegrambot.services;

import com.example.telegrambot.entity.BotUsers;
import com.example.telegrambot.repository.BotUsersRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class CallbackService extends DefaultAbsSender implements CallbackServiceInterface{

    @Value("${bot.token}")
    private String token;

    private final BotUsersRepos botUsersRepos;
    private final InlineKeyboardServiceInterface inlineKeyboardServiceInterface;

    public CallbackService(BotUsersRepos botUsersRepos, InlineKeyboardService inlineKeyboardServiceInterface) {
        super(new DefaultBotOptions());
        this.botUsersRepos = botUsersRepos;
        this.inlineKeyboardServiceInterface = inlineKeyboardServiceInterface;
    }

    @Override
    public void getCallback(CallbackQuery callbackQuery) throws TelegramApiException {
        if(callbackQuery.getData().matches("/sub/(.*)"))
        {
            String userId = callbackQuery.getData().substring(5);
           if(botUsersRepos.findById(Long.valueOf(userId)).isPresent()) {
                BotUsers botUsersToAddSub = botUsersRepos.findById(Long.valueOf(userId)).get();
                if(botUsersRepos.findById(callbackQuery.getFrom().getId()).isPresent()){
                    BotUsers checkUser=botUsersToAddSub.getSubUser().stream()
                            .filter(botUsers -> callbackQuery.getFrom().getId().equals(botUsers.getId()))
                            .findFirst()
                            .orElse(null);
                    if(checkUser==null) {
                        botUsersToAddSub.getSubUser().add(botUsersRepos.findById(callbackQuery.getFrom().getId()).get());
                        botUsersRepos.save(botUsersToAddSub);
                        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
                        editMessageReplyMarkup.setChatId(String.valueOf(callbackQuery.getFrom().getId()));
                        editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
                        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardServiceInterface.sendUnsubInlineKeyboard(botUsersToAddSub));
                        execute(editMessageReplyMarkup);
                    }
                }
            }
        }else
        if(callbackQuery.getData().matches("/unsub/(.*)"))
        {
            String userId = callbackQuery.getData().substring(7);
            if (botUsersRepos.findById(callbackQuery.getFrom().getId()).isPresent()) {
                if (botUsersRepos.findById(Long.valueOf(userId)).isPresent()) {
                    BotUsers botUserToRemuveSub = botUsersRepos.findById(Long.valueOf(userId)).get();
                    for (int i = 0; i < botUserToRemuveSub.getSubUser().size(); i++) {
                        if(botUserToRemuveSub.getSubUser().get(i).getId().equals(callbackQuery.getFrom().getId())){
                            botUserToRemuveSub.getSubUser().remove(i);
                        }
                    }
                    botUsersRepos.save(botUserToRemuveSub);
                    EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
                    editMessageReplyMarkup.setChatId(String.valueOf(callbackQuery.getFrom().getId()));
                    editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
                    editMessageReplyMarkup.setReplyMarkup(inlineKeyboardServiceInterface.sendSubInlineKeyboard(botUserToRemuveSub));
                    execute(editMessageReplyMarkup);
                }
            }
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
