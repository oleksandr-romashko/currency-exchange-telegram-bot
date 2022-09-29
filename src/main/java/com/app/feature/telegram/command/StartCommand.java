package com.app.feature.telegram.command;

import com.app.feature.currency.dto.Bank;
import com.app.feature.currency.dto.Currency;
import com.app.feature.telegram.PropertiesConstants;
import com.app.feature.user.UserInfo;
import com.app.feature.user.UserUtil;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StartCommand extends BotCommand {
    private static final String BOT_NAME = "Currency Exchange Bot";
    public static final String GET_INFO_BUTTON_TEXT = "Get info";
    public static final String SETTING_BUTTON_TEXT = "Settings";
    private static final String GREETING_MESSAGE = "Hello, ${userName}!"
            + "\n\n"
            + "I am an automatic system \"${botName}\" that will help you track current exchange rates."
            + "\n"
            + "The project is maintained and supported here."
            + "\n\n"
            + "If you enjoy using me, you might want to share my contact with others."
            + "\n\n"
            + "Please click on the \"${getInfoButtonText}\" button below to get information on exchange rates."
            + "\n\n"
            + "By clicking on the \"${SettingsButtonText}\" button, you can configure which currencies and which banks to display,"
            + " as well as how to display them."
            + " There you can also set the time of daily notifications or turn them off."
            + "\n"
            + "So that you can already use my services, I have already pre-set some settings for you.";

    public StartCommand() {
        super("start", "With this command you can start the Bot");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        System.out.println("Start pressed!");
        registerUser(user);
        SendMessage message = createGreetingMessage(user.getFirstName());
        onMenuPressed(message, chat.getId().toString(), absSender);
    }

    private void registerUser(User user) {
        List<UserInfo> usersList = new UserUtil().read();
        Long userId = user.getId();
        if(usersList.stream()
                .map(UserInfo::getChatId)
                .filter(it -> Objects.equals(it, userId))
                .findFirst().isEmpty()) {
            String firstName = user.getFirstName();
            String userName = user.getUserName();
            Currency currency = Currency.valueOf(new PropertiesConstants().propertiesReader("user.default.currency"));
            Bank bank = Bank.valueOf(new PropertiesConstants().propertiesReader("user.default.bank"));
            int rounding = Integer.parseInt(new PropertiesConstants().propertiesReader("user.default.rounding"));
            String alarmTime = new PropertiesConstants().propertiesReader("user.default.alarm_time");

            UserInfo newUser = new UserInfo(userId, firstName, userName, currency, bank, rounding, alarmTime);
            usersList.add(newUser);
            new UserUtil().write(usersList);
        }
    }

    private SendMessage createGreetingMessage(String userName) {
        String text = GREETING_MESSAGE
                .replace("${userName}", userName)
                .replace("${botName}", BOT_NAME)
                .replace("${getInfoButtonText}", GET_INFO_BUTTON_TEXT)
                .replace("${SettingsButtonText}", SETTING_BUTTON_TEXT);

        List<MessageEntity> entities = new ArrayList<>();

        MessageEntity botNameEntity = new MessageEntity();
        botNameEntity.setType("bold");
        botNameEntity.setOffset(text.indexOf('\"' + BOT_NAME) + 1);
        botNameEntity.setLength(BOT_NAME.length());
        entities.add(botNameEntity);

        String supportLinkUrl = String.valueOf(new PropertiesConstants().propertiesReader("project.support.repository"));
        if (!supportLinkUrl.isBlank()) {
            MessageEntity supportEntity = new MessageEntity();
            supportEntity.setType("text_link");
            supportEntity.setUrl(supportLinkUrl);
            supportEntity.setOffset(text.indexOf("supported here") + 10);
            supportEntity.setLength("here".length());
            entities.add(supportEntity);
        }

        String myContact = String.valueOf(new PropertiesConstants().propertiesReader("project.support.my_contact"));
        if (!myContact.isBlank()) {
            MessageEntity shareContactEntity = new MessageEntity();
            shareContactEntity.setType("text_link");
            shareContactEntity.setUrl(myContact);
            shareContactEntity.setOffset(text.indexOf("share my contact"));
            shareContactEntity.setLength("share my contact".length());
            entities.add(shareContactEntity);
        }

        MessageEntity getInfoEntity = new MessageEntity();
        getInfoEntity.setType("bold");
        getInfoEntity.setOffset(text.indexOf('\"' + GET_INFO_BUTTON_TEXT) + 1);
        getInfoEntity.setLength(GET_INFO_BUTTON_TEXT.length());
        entities.add(getInfoEntity);

        MessageEntity settingsEntity = new MessageEntity();
        settingsEntity.setType("bold");
        settingsEntity.setOffset(text.indexOf('\"' + SETTING_BUTTON_TEXT) + 1);
        settingsEntity.setLength(SETTING_BUTTON_TEXT.length());
        entities.add(settingsEntity);

        SendMessage message = new SendMessage();
        message.setEntities(entities);
        message.setText(text);
        message.disableWebPagePreview();

        return message;
    }

    public void onMenuPressed(SendMessage message, String chatId, AbsSender absSender) {
        InlineKeyboardButton getInfoButton = InlineKeyboardButton.builder().text(GET_INFO_BUTTON_TEXT).callbackData("get_info").build();
        InlineKeyboardButton settingsButton = InlineKeyboardButton.builder().text(SETTING_BUTTON_TEXT).callbackData("settings").build();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>(List.of(List.of(getInfoButton), List.of(settingsButton)));
        sendMessageWithKeyboard(message, chatId, createKeyboard(rowList),absSender);
    }

    private void sendMessageWithKeyboard(SendMessage message, String chatId, InlineKeyboardMarkup keyboard, AbsSender absSender) {
        message.setChatId(chatId);
        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createKeyboard(List<List<InlineKeyboardButton>> rowList) {
        return InlineKeyboardMarkup
                .builder()
                .keyboard(rowList)
                .build();
    }
}
