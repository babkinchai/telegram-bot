package com.example.telegrambot.services;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface PhotoServiceInterface {

    public void sendCatsPhoto(Message message);

    void savePhoto(Message message) throws TelegramApiException;
}
