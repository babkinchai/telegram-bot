package com.example.telegrambot.services;

import com.example.telegrambot.entity.BotUsers;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface InlineKeyboardServiceInterface {
    InlineKeyboardMarkup sendSubInlineKeyboard(BotUsers botUsers);
    InlineKeyboardMarkup sendUnsubInlineKeyboard(BotUsers botUsers);
}
