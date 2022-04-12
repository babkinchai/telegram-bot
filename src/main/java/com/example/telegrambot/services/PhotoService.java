package com.example.telegrambot.services;

import com.example.telegrambot.entity.BotUsers;
import com.example.telegrambot.entity.UsersCatImage;
import com.example.telegrambot.repository.BotUsersRepos;
import com.example.telegrambot.repository.UsersCatImageRepos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class PhotoService extends DefaultAbsSender implements PhotoServiceInterface {

    public static Logger logger= LoggerFactory.getLogger(PhotoService.class);

    @Value("${bot.token}")
    private String token;

    private final UsersCatImageRepos usersCatImageRepos;
    private final BotUsersRepos botUsersRepos;

    public PhotoService(UsersCatImageRepos usersCatImageRepos, BotUsersRepos botUsersRepos) {
        super(new DefaultBotOptions());
        this.usersCatImageRepos = usersCatImageRepos;
        this.botUsersRepos = botUsersRepos;
    }

    @Override
    public InputFile getPhoto() throws IndexOutOfBoundsException{
        return new InputFile( usersCatImageRepos.getRundom().get(0).getFileId());
    }

    @Override
    public void savePhoto(Message message) throws TelegramApiException {
        File fileDir = new File("img");
        boolean mkdir = fileDir.mkdir();
        if(!mkdir){
            logger.error("Cant create /img directory");
            message.setText("Загрузка фото временно не работает");
        }
        else {
            try {
                File file = new File("img/" + message.getPhoto().get(2).getFileId() + ".jpg");
                if (file.createNewFile()) {
                    downloadFile(Objects.requireNonNull(getFilePath(message.getPhoto().get(2))), file);
                    message.setText(saveImage(message, file));
                    file.deleteOnExit();
                } else {
                    message.setText("Не судьба");
                }
            } catch (NoSuchElementException e) {
                message.setText("Наберите /start для начала");
            } catch (IOException e) {
                logger.error("Cant create new file." + e.getMessage());
                e.printStackTrace();
            }
        }
        execute(new SendMessage(message.getChatId().toString(),message.getText()));
    }

    private String getFilePath(PhotoSize photo) {
        Objects.requireNonNull(photo);

        if (photo.getFilePath()!=null) {
            return photo.getFilePath();
        } else {
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(photo.getFileId());
            try {
                org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);
                return file.getFilePath();
            } catch (TelegramApiException e) {
                logger.error("getFilePath method error. Cant send message for client. "+e.getMessage());
            }
        }

        return null;
    }

    public String saveImage(Message message, File file) throws NoSuchElementException {
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
            List<PhotoSize> photo = message.getPhoto();
            UsersCatImage usersCatImage = new UsersCatImage();
            usersCatImage.setImageName(md5);
            usersCatImage.setFileId(message.getPhoto().get(2).getFileId());
            file.deleteOnExit();
            Optional<BotUsers> byId = botUsersRepos.findById(message.getFrom().getId());
            usersCatImage.setBotUsers(byId.orElseThrow());
            usersCatImageRepos.save(usersCatImage);
            return "Изображение сохранено";
        }
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
