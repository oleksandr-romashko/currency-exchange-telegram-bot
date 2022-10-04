package com.app.feature.telegram.command;

import com.app.feature.telegram.CurrencyTelegramBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class GetInfoCommand extends BotCommand {
    private CurrencyTelegramBot currencyTelegramBot;

    public GetInfoCommand() {
        super("getinfo", "Get current currency rates");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        currencyTelegramBot = new CurrencyTelegramBot(chat.getId());
        currencyTelegramBot.onGetInfoPressed();
    }
}
