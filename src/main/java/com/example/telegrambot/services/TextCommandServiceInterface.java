package com.example.telegrambot.services;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TextCommandServiceInterface {

    void getTextMessage(Message update);

    String startBotMessage(Message update);
}
