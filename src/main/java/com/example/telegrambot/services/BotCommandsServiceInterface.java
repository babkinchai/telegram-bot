package com.example.telegrambot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;

public interface BotCommandsServiceInterface {

    String startBotMessage(Update update);

    String saveImage(Update update, File file);

    InputFile getPhoto();

    String getImagePath(String url) throws JsonProcessingException;
}
