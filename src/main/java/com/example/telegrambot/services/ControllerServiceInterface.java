package com.example.telegrambot.services;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ControllerServiceInterface {
    void sendMesForAllUser(String message);
}
