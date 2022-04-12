package com.example.telegrambot.services;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackServiceInterface {

    void getCallback(CallbackQuery callbackQuery);
}
