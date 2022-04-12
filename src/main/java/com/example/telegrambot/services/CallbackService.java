package com.example.telegrambot.services;

import com.example.telegrambot.entity.BotUsers;
import com.example.telegrambot.repository.BotUsersRepos;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
public class CallbackService implements CallbackServiceInterface{

    private final BotUsersRepos botUsersRepos;

    public CallbackService(BotUsersRepos botUsersRepos) {
        this.botUsersRepos = botUsersRepos;
    }

    @Override
    public void getCallback(CallbackQuery callbackQuery) {
        if(callbackQuery.getData().matches("/sub/(.*)"))
        {
            String username = callbackQuery.getData().substring(5);
            if(botUsersRepos.findByUsername(username).isPresent()) {
                BotUsers botUsers = botUsersRepos.findByUsername(username).get();
                if(botUsersRepos.findById(callbackQuery.getFrom().getId()).isPresent()){
                    BotUsers checkUser=botUsers.getSubUser().stream()
                            .filter(botUsers1 -> callbackQuery.getFrom().getUserName().equals(botUsers1.getUsername()))
                            .findFirst()
                            .orElse(null);
                    if(checkUser==null) {
                        botUsers.getSubUser().add(botUsersRepos.findById(callbackQuery.getFrom().getId()).get());
                        botUsersRepos.save(botUsers);
                    }
                }
            }
        }else
        if(callbackQuery.getData().matches("/unsub/(.*)"))
        {
            String username = callbackQuery.getData().substring(7);
            if (botUsersRepos.findById(callbackQuery.getFrom().getId()).isPresent()) {
                if (botUsersRepos.findByUsername(username).isPresent()) {
                    BotUsers botUsers = botUsersRepos.findByUsername(username).get();
                    for (int i = 0; i < botUsers.getSubUser().size(); i++) {
                        if(botUsers.getSubUser().get(i).getId().equals(callbackQuery.getFrom().getId())){
                            botUsers.getSubUser().remove(i);
                        }
                    }
                    botUsersRepos.save(botUsers);
                }
            }
        }
    }
}
