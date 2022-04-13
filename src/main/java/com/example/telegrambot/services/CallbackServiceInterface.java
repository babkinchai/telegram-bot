package com.example.telegrambot.services;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface CallbackServiceInterface {

    void getCallback(CallbackQuery callbackQuery) throws TelegramApiException;
}
