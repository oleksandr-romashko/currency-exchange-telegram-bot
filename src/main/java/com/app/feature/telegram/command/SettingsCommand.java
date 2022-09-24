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

public class SettingsCommand extends BotCommand {
    public SettingsCommand() {
        super("settings", "With this command you can customize your bot");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        System.out.println("Settings pressed!");
        String text = "Settings";
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chat.getId().toString());

        KeyboardButton decimalPlacesButton = KeyboardButton.builder().text("/decimal_places").build();
        KeyboardButton currencyButton = KeyboardButton.builder().text("/currency").build();
        KeyboardButton notificationTimeButton = KeyboardButton.builder().text("/notification_time").build();
        KeyboardButton bankButton = KeyboardButton.builder().text("/bank").build();

        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(decimalPlacesButton);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(currencyButton);

        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(notificationTimeButton);

        KeyboardRow keyboardRow4 = new KeyboardRow();
        keyboardRow4.add(bankButton);

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup
                .builder()
                .keyboardRow(keyboardRow1)
                .keyboardRow(keyboardRow2)
                .keyboardRow(keyboardRow3)
                .keyboardRow(keyboardRow4)
                .build();

        message.setReplyMarkup(keyboard);

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
