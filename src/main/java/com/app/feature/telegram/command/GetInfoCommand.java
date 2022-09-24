package com.app.feature.telegram.command;

import com.app.feature.currency.CurrencyService;
import com.app.feature.currency.PrivatBankCurrencyService;
import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItem;
import com.app.feature.telegram.ui.PrettyPrintCurrencyService;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class GetInfoCommand extends BotCommand {
    public GetInfoCommand() {
        super("get_info", "With this command you can get the current exchange rate");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        System.out.println("Get_info pressed!");
        CurrencyService currencyService = new PrivatBankCurrencyService();
        PrettyPrintCurrencyService prettyPrintCurrencyService = new PrettyPrintCurrencyService();
        Currency currency = Currency.USD;

        CurrencyItem currencyRate = null;
        try {
            currencyRate = currencyService.getRate(currency);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert currencyRate != null;
        String prettyText = prettyPrintCurrencyService.convert(currencyRate, currency);
        SendMessage message = new SendMessage();
        message.setText(prettyText);
        message.setChatId(chat.getId().toString());

        KeyboardButton getInfoButton = KeyboardButton.builder().text("/get_info").build();
        KeyboardButton settingsButton = KeyboardButton.builder().text("/settings").build();

        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(getInfoButton);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(settingsButton);

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup
                .builder()
                .keyboardRow(keyboardRow1)
                .keyboardRow(keyboardRow2)
                .build();

        message.setReplyMarkup(keyboard);

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
