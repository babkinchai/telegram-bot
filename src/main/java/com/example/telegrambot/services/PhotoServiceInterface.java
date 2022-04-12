package com.example.telegrambot.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface PhotoServiceInterface {

    InputFile getPhoto() throws IndexOutOfBoundsException;

    void savePhoto(Message message) throws TelegramApiException;
}
