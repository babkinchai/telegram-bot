package com.example.telegrambot.controller;

import com.example.telegrambot.services.ControllerService;
import com.example.telegrambot.services.ControllerServiceInterface;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RestController
public class Controllers {

    private final ControllerServiceInterface controllerServiceInterface;

    public Controllers(ControllerService controllerServiceInterface) {
        this.controllerServiceInterface = controllerServiceInterface;
    }

    @GetMapping("/send")
    public String sendEndpoint(@RequestParam String message) {
        controllerServiceInterface.sendMesForAllUser(message);
        return message + "send";
    }
}
