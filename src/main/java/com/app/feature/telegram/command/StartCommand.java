package com.app.feature.telegram.command;


import com.app.feature.currency.dto.Bank;
import com.app.feature.currency.dto.Currency;
import com.app.feature.telegram.PropertiesConstants;
import com.app.feature.user.UserInfo;
import com.app.feature.user.UserUtil;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StartCommand extends BotCommand {
    public StartCommand() {
        super("start", "With this command you can start the Bot");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        System.out.println("Start pressed!");
        registerUser(user);
        String text = "Welcome! This bot will help you track current exchange rates...";
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chat.getId().toString());

        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>();
        InlineKeyboardButton getInfoButton = InlineKeyboardButton.builder().text("Get info").callbackData("get_info").build();
        InlineKeyboardButton settingsButton = InlineKeyboardButton.builder().text("Settings").callbackData("settings").build();
        keyboardRow1.add(getInfoButton);
        keyboardRow2.add(settingsButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardRow1);
        rowList.add(keyboardRow2);

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup
                .builder()
                .keyboard(rowList)
                .build();

        message.setReplyMarkup(keyboard);

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
}
