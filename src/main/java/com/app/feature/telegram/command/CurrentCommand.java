package com.app.feature.telegram.command;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CurrentCommand extends BotCommand {
public CurrentCommand() {
    super("current", "With this command you can get current exchange rates");
}

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        System.out.println("Current pressed!");
        String text = "Показуємо курси валют і банків, які Ви вказали в налаштуваннях";
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chat.getId().toString());

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
