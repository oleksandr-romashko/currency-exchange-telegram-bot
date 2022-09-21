package com.app.feature.telegram.command;


import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartCommand extends BotCommand {
    public StartCommand() {
        super("start", "With this command you can start the Bot");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        System.out.println("Start pressed!");
        String text = "Use menu buttons for navigate:";
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chat.getId().toString());

        KeyboardButton currentButton = KeyboardButton.builder().text("/current").build();
        KeyboardButton dayRateButton = KeyboardButton.builder().text("/day_rate").build();
        KeyboardButton currencyButton = KeyboardButton.builder().text("/currency").build();
        KeyboardButton calcButton = KeyboardButton.builder().text("/calculator").build();
        KeyboardButton bankButton = KeyboardButton.builder().text("/bank").build();
        KeyboardButton graphicsButton = KeyboardButton.builder().text("/graphics").build();
        KeyboardButton settingsButton = KeyboardButton.builder().text("/settings").build();

        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(currentButton);
        keyboardRow1.add(dayRateButton);
        keyboardRow1.add(currencyButton);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(calcButton);
        keyboardRow2.add(bankButton);
        keyboardRow2.add(graphicsButton);

        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(settingsButton);

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup
                .builder()
                .keyboardRow(keyboardRow1)
                .keyboardRow(keyboardRow2)
                .keyboardRow(keyboardRow3)
                .build();

        message.setReplyMarkup(keyboard);

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
