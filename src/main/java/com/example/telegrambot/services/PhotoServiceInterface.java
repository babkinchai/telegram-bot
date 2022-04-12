package com.example.telegrambot.services;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface PhotoServiceInterface {

    SendPhoto getPhoto(Message message) throws IndexOutOfBoundsException;

    void savePhoto(Message message) throws TelegramApiException;
}
