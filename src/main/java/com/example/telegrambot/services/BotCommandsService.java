/*
package com.example.telegrambot.services;

import com.example.telegrambot.domain.FilePathDomain;
import com.example.telegrambot.entity.BotUsers;
import com.example.telegrambot.entity.UsersCatImage;
import com.example.telegrambot.repository.BotUsersRepos;
import com.example.telegrambot.repository.UsersCatImageRepos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BotCommandsService implements BotCommandsServiceInterface {

    private final BotUsersRepos botUsersRepos;
    private final UsersCatImageRepos usersCatImageRepos;

    public BotCommandsService(BotUsersRepos botUsersRepos, UsersCatImageRepos usersCatImageRepos) {
        this.botUsersRepos = botUsersRepos;
        this.usersCatImageRepos = usersCatImageRepos;
    }

    public String startBotMessage(Update update) {
        File file=new File("img/");
        file.mkdir();
        BotUsers botUsers=new BotUsers();
        botUsers.setUsername(update.getMessage().getFrom().getUserName());
        botUsers.setId(update.getMessage().getFrom().getId());
        botUsersRepos.save(botUsers);
        return "Hello my dear, "+botUsers.getUsername();
    }

    @Override
    public String saveImage(Update update,File file) throws NoSuchElementException {
        String md5="";
        try (InputStream is = Files.newInputStream(file.toPath())) {
            md5= org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(usersCatImageRepos.existsUsersCatImageByImageName(md5)){
            return "Прости, но такое изображение уже есть";
        }
        else {
            File newFile=new File("img/"+md5+".jpg");
            List<PhotoSize> photo = update.getMessage().getPhoto();
            UsersCatImage usersCatImage = new UsersCatImage();
            usersCatImage.setImageName(md5);
            boolean b = file.renameTo(newFile);
            Optional<BotUsers> byId = botUsersRepos.findById(update.getMessage().getFrom().getId());
            usersCatImage.setBotUsers(byId.orElseThrow());
            usersCatImageRepos.save(usersCatImage);
            return "Изображение сохранено";
        }
    }

    @Override
    public InputFile getPhoto() {
        InputFile inputFile=new InputFile();
        File file=new File("img/"+usersCatImageRepos.getRundom().get(0).getImageName()+".jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputFile.setMedia(file);
        return inputFile;
    }

    @Override
    public String getImagePath(String url) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String photoSize=restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper=new ObjectMapper();
        FilePathDomain photoSizeObject = objectMapper.readValue(photoSize, FilePathDomain.class);
        return photoSizeObject.getResult().getFile_path();
    }
}
*/
