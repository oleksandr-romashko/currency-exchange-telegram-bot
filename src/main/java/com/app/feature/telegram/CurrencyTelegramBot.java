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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyTelegramBot extends TelegramLongPollingCommandBot {
    private CurrencyService currencyService;
    private PrettyPrintCurrencyService prettyPrintCurrencyService;

    public CurrencyTelegramBot() {
        currencyService = new PrivatBankCurrencyService();
        prettyPrintCurrencyService = new PrettyPrintCurrencyService();

        register(new StartCommand());
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            SendMessage responseMessage = new SendMessage();
            if (update.getCallbackQuery().getData().equals("getInfo")) {
                System.out.println("Get_info pressed!");
                Currency currency = Currency.USD;

                CurrencyItem currencyRate = null;
                try {
                    currencyRate = currencyService.getRate(currency);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert currencyRate != null;
                String prettyText = prettyPrintCurrencyService.convert(currencyRate, currency);

                responseMessage.setText(prettyText);
                responseMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());

                List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>();
                List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>();
                InlineKeyboardButton getInfoButton = InlineKeyboardButton.builder().text("Get info").callbackData("getInfo").build();
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

                responseMessage.setReplyMarkup(keyboard);

            } else if (update.getCallbackQuery().getData().equals("settings")) {
                System.out.println("Settings pressed!");
                responseMessage.setText("Settings");
                responseMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());

                List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>();
                List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>();
                List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>();
                List<InlineKeyboardButton> keyboardRow4 = new ArrayList<>();
                InlineKeyboardButton decimalPlacesButton = InlineKeyboardButton.builder().text("Number of decimal places").callbackData("Number_of_decimal_places").build();
                InlineKeyboardButton currencyButton = InlineKeyboardButton.builder().text("Currency").callbackData("currency").build();
                InlineKeyboardButton notificationTimeButton = InlineKeyboardButton.builder().text("Notification time").callbackData("notification_time").build();
                InlineKeyboardButton bankButton = InlineKeyboardButton.builder().text("Bank").callbackData("bank").build();
                keyboardRow1.add(decimalPlacesButton);
                keyboardRow2.add(currencyButton);
                keyboardRow3.add(notificationTimeButton);
                keyboardRow4.add(bankButton);

                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(keyboardRow1);
                rowList.add(keyboardRow2);
                rowList.add(keyboardRow3);
                rowList.add(keyboardRow4);

                InlineKeyboardMarkup keyboard = InlineKeyboardMarkup
                        .builder()
                        .keyboard(rowList)
                        .build();

                responseMessage.setReplyMarkup(keyboard);

            } else {
                System.out.println("Non-command here!");
            }

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
