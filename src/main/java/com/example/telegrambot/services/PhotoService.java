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
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

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
    public SendPhoto getPhoto(Message message) throws IndexOutOfBoundsException{
        SendPhoto sendPhoto = new SendPhoto();
        UsersCatImage usersCatImage = usersCatImageRepos.getRundom().get(0);
        sendPhoto.setPhoto(new InputFile(usersCatImage.getFileId()));
        if(!message.getFrom().getUserName().equals(usersCatImage.getBotUsers().getUsername())) {
            sendPhoto.setReplyMarkup(sendInlineKeyboard(usersCatImage.getBotUsers().getUsername()));
        }
        return sendPhoto;
    }

    @Override
    public void savePhoto(Message message) throws TelegramApiException {
        File fileDir = new File("img");
        boolean mkdir = fileDir.mkdir();
            try {
                File file = new File("img/" + message.getPhoto().get(2).getFileId() + ".jpg");
                file.createNewFile();
                    downloadFile(Objects.requireNonNull(getFilePath(message.getPhoto().get(2))), file);
                    message.setText(saveImage(message, file));
                    file.deleteOnExit();
            } catch (NoSuchElementException e) {
                message.setText("Наберите /start для начала");
            } catch (IOException e) {
                logger.error("Cant create new file." + e.getMessage());
                e.printStackTrace();
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
            UsersCatImage usersCatImage = new UsersCatImage();
            usersCatImage.setImageName(md5);
            usersCatImage.setFileId(message.getPhoto().get(2).getFileId());
            file.deleteOnExit();
            BotUsers byId = botUsersRepos.findById(message.getFrom().getId()).get();
            usersCatImage.setBotUsers(byId);
            usersCatImageRepos.save(usersCatImage);
            return "Изображение сохранено";
        }
    }

    public InlineKeyboardMarkup sendInlineKeyboard(String username) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<InlineKeyboardButton>();
        InlineKeyboardButton sub= new InlineKeyboardButton("Подписаться на публикации " + username);
        sub.setCallbackData("/sub/"+username);
        buttons.add(sub);
        keyboard.add(buttons);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
