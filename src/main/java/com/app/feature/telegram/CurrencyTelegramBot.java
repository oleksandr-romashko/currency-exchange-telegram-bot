package com.app.feature.telegram;

import com.app.feature.currency.CurrencyService;
import com.app.feature.currency.PrivatBankCurrencyService;
import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItem;
import com.app.feature.telegram.command.*;
import com.app.feature.telegram.ui.PrettyPrintCurrencyService;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class CurrencyTelegramBot extends TelegramLongPollingCommandBot {
    private CurrencyService currencyService;
    private PrettyPrintCurrencyService prettyPrintCurrencyService;

    public CurrencyTelegramBot() {
        currencyService = new PrivatBankCurrencyService();
        prettyPrintCurrencyService = new PrettyPrintCurrencyService();

        register(new StartCommand());
        register(new GetInfoCommand());
        register(new SettingsCommand());
        register(new BankCommand());
        register(new CurrencyCommand());
        register(new NotificationTimeCommand());
        register(new DecimalPlacesCommand());
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if(update.hasCallbackQuery()) {
            String callbackQuery = update.getCallbackQuery().getData();

            Currency currency = Currency.valueOf(callbackQuery);

            CurrencyItem currencyRate = null;
            try {
                currencyRate = currencyService.getRate(currency);
            } catch (IOException e) {
                e.printStackTrace();
            }


            assert currencyRate != null;
            String prettyText = prettyPrintCurrencyService.convert(currencyRate, currency);

            SendMessage responseMessage = new SendMessage();
            responseMessage.setText(prettyText);
            responseMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());

            try {
                execute(responseMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else
        System.out.println("Non-command here!");
    }

    @Override
    public String getBotUsername() {
        return new BotConstants().propertiesReader("bot.name");
    }

    @Override
    public String getBotToken() {
        return new BotConstants().propertiesReader("bot.token");
    }
}
