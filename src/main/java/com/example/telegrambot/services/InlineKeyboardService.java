package com.example.telegrambot.services;

import com.example.telegrambot.entity.BotUsers;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class InlineKeyboardService implements InlineKeyboardServiceInterface{
    public InlineKeyboardMarkup sendSubInlineKeyboard(BotUsers botUsers) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<InlineKeyboardButton>();
        InlineKeyboardButton sub= new InlineKeyboardButton("Подписаться на публикации " + (botUsers.getUsername() == null ?"":botUsers.getUsername()));
        sub.setCallbackData("/sub/"+botUsers.getId());
        buttons.add(sub);
        keyboard.add(buttons);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup sendUnsubInlineKeyboard(BotUsers botUsers) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<InlineKeyboardButton>();
        InlineKeyboardButton sub= new InlineKeyboardButton("Отписаться от публикаций " + (botUsers.getUsername() == null ?"":botUsers.getUsername()));
        sub.setCallbackData("/unsub/"+botUsers.getId());
        buttons.add(sub);
        keyboard.add(buttons);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }
}
