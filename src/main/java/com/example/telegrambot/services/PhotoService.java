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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

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

    public SendPhoto getPhoto(Message message) throws IndexOutOfBoundsException{
        UsersCatImage usersCatImage = usersCatImageRepos.getRundom().get(0);
        return addSubMenu(message.getFrom().getId(), usersCatImage);
    }

    private SendPhoto addSubMenu(Long fromId, UsersCatImage usersCatImage) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(new InputFile(usersCatImage.getFileId()));
        if(!fromId.equals(usersCatImage.getBotUsers().getId())) {
            if(botUsersRepos.findById(fromId).isPresent()) {
                BotUsers checkUser = usersCatImage.getBotUsers().getSubUser().stream().filter(botUsers -> botUsers.getId().equals(fromId)).findFirst().orElse(null);
                if (checkUser!=null){
                    sendPhoto.setReplyMarkup(sendUnSubInlineKeyboard(usersCatImage.getBotUsers().getUsername()));
                }
                else {
                    sendPhoto.setReplyMarkup(sendSubInlineKeyboard(usersCatImage.getBotUsers().getUsername()));
                }
            }
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
            byId.getSubUser().forEach(botUsers -> {
                SendPhoto sendPhoto=addSubMenu(botUsers.getId(), usersCatImage);
                sendPhoto.setChatId(botUsers.getId().toString());
                try {
                    execute(sendPhoto);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            });
            return "Изображение сохранено";
        }
    }

    public InlineKeyboardMarkup sendSubInlineKeyboard(String username) {
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

    public InlineKeyboardMarkup sendUnSubInlineKeyboard(String username) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<InlineKeyboardButton>();
        InlineKeyboardButton sub= new InlineKeyboardButton("Отписаться от публикаций " + username);
        sub.setCallbackData("/unsub/"+username);
        buttons.add(sub);
        keyboard.add(buttons);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void sendCatsPhoto(Message message) {
        try {
            SendPhoto sendPhoto;
            try {
                sendPhoto=getPhoto(message);
                sendPhoto.setChatId(message.getChatId().toString());
                execute(sendPhoto);
            } catch (IndexOutOfBoundsException e) {
                execute(new SendMessage(message.getChatId().toString(),"Нет фоток кота"));
            }
        }
        catch (TelegramApiException e) {
            logger.error("sendCatsPhoto switch default error. Cant send message for client. "+e.getMessage());
        }
    }
}
