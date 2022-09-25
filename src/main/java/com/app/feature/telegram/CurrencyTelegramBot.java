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
    private String chatId;

    public CurrencyTelegramBot() {
        currencyService = new PrivatBankCurrencyService();
        prettyPrintCurrencyService = new PrettyPrintCurrencyService();

        register(new StartCommand());
    }

    private void sendMessageWithKeyboard(String text, String chatId, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chatId);
        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void onGetInfoPressed() {
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

        sendMessageWithKeyboard(prettyText, chatId, keyboard);
    }

    private void onSettingsPressed() {
        System.out.println("Settings pressed!");

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

        sendMessageWithKeyboard("Settings", chatId, keyboard);
    }

    private void onDecimalPlacesPressed() {
        System.out.println("Decimal_places pressed!");

        String text = "Select the number of decimal places:";
        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>();
        InlineKeyboardButton twoDecimalPlacesButton = InlineKeyboardButton.builder().text("2").callbackData("two_decimal_places").build();
        InlineKeyboardButton threeDecimalPlacesButton = InlineKeyboardButton.builder().text("3").callbackData("three_decimal_places").build();
        InlineKeyboardButton fourDecimalPlacesButton = InlineKeyboardButton.builder().text("4").callbackData("four_decimal_places").build();
        keyboardRow1.add(twoDecimalPlacesButton);
        keyboardRow2.add(threeDecimalPlacesButton);
        keyboardRow3.add(fourDecimalPlacesButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardRow1);
        rowList.add(keyboardRow2);
        rowList.add(keyboardRow3);

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup
                .builder()
                .keyboard(rowList)
                .build();

        sendMessageWithKeyboard(text, chatId, keyboard);
    }

    private void onCurrencyPressed() {
        System.out.println("Currency pressed!");

        String text = "Select the currency/currencies:";
        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>();
        InlineKeyboardButton usdButton = InlineKeyboardButton.builder().text("USD").callbackData("USD").build();
        InlineKeyboardButton eurButton = InlineKeyboardButton.builder().text("EUR").callbackData("EUR").build();
        InlineKeyboardButton rubButton = InlineKeyboardButton.builder().text("RUB").callbackData("RUB").build();
        keyboardRow1.add(usdButton);
        keyboardRow2.add(eurButton);
        keyboardRow3.add(rubButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardRow1);
        rowList.add(keyboardRow2);
        rowList.add(keyboardRow3);

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup
                .builder()
                .keyboard(rowList)
                .build();

        sendMessageWithKeyboard(text, chatId, keyboard);
    }

    private void onNotificationTimePressed() {
        System.out.println("Notification_time pressed!");

        String text = "Select notification time:";
        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow4 = new ArrayList<>();
        InlineKeyboardButton nineButton = InlineKeyboardButton.builder().text("9").callbackData("9").build();
        InlineKeyboardButton tenButton = InlineKeyboardButton.builder().text("10").callbackData("10").build();
        InlineKeyboardButton elevenButton = InlineKeyboardButton.builder().text("11").callbackData("11").build();
        InlineKeyboardButton twelveButton = InlineKeyboardButton.builder().text("12").callbackData("12").build();
        InlineKeyboardButton thirteenButton = InlineKeyboardButton.builder().text("13").callbackData("13").build();
        InlineKeyboardButton fourteenButton = InlineKeyboardButton.builder().text("14").callbackData("14").build();
        InlineKeyboardButton fifteenButton = InlineKeyboardButton.builder().text("15").callbackData("15").build();
        InlineKeyboardButton sixteenButton = InlineKeyboardButton.builder().text("16").callbackData("16").build();
        InlineKeyboardButton seventeenButton = InlineKeyboardButton.builder().text("17").callbackData("17").build();
        InlineKeyboardButton eighteenButton = InlineKeyboardButton.builder().text("18").callbackData("18").build();
        InlineKeyboardButton turnOffNotificationsButton = InlineKeyboardButton.builder().text("Turn off notifications").callbackData("turn_off_notifications").build();
        keyboardRow1.add(nineButton);
        keyboardRow1.add(tenButton);
        keyboardRow1.add(elevenButton);
        keyboardRow2.add(twelveButton);
        keyboardRow2.add(thirteenButton);
        keyboardRow2.add(fourteenButton);
        keyboardRow3.add(fifteenButton);
        keyboardRow3.add(sixteenButton);
        keyboardRow3.add(seventeenButton);
        keyboardRow4.add(eighteenButton);
        keyboardRow4.add(turnOffNotificationsButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardRow1);
        rowList.add(keyboardRow2);
        rowList.add(keyboardRow3);
        rowList.add(keyboardRow4);

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup
                .builder()
                .keyboard(rowList)
                .build();

        sendMessageWithKeyboard(text, chatId, keyboard);
    }

    private void onBankPressed() {
        System.out.println("Bank pressed!");

        String text = "Select the bank/banks:";
        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>();
        InlineKeyboardButton nbuButton = InlineKeyboardButton.builder().text("NBU").callbackData("NBU").build();
        InlineKeyboardButton privatBankButton = InlineKeyboardButton.builder().text("PrivatBank").callbackData("PrivatBank").build();
        InlineKeyboardButton monoBankButton = InlineKeyboardButton.builder().text("MonoBank").callbackData("MonoBank").build();
        keyboardRow1.add(nbuButton);
        keyboardRow2.add(privatBankButton);
        keyboardRow3.add(monoBankButton);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardRow1);
        rowList.add(keyboardRow2);
        rowList.add(keyboardRow3);

        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup
                .builder()
                .keyboard(rowList)
                .build();

        sendMessageWithKeyboard(text, chatId, keyboard);
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        if (update.hasCallbackQuery()) {
            if (update.getCallbackQuery().getData().equals("get_info")) {
                onGetInfoPressed();
            } else if (update.getCallbackQuery().getData().equals("settings")) {
                onSettingsPressed();
            } else if (update.getCallbackQuery().getData().equals("Number_of_decimal_places")) {
                onDecimalPlacesPressed();
            } else if (update.getCallbackQuery().getData().equals("notification_time")) {
                onNotificationTimePressed();
            } else if (update.getCallbackQuery().getData().equals("currency")) {
                onCurrencyPressed();
            } else if (update.getCallbackQuery().getData().equals("bank")) {
                onBankPressed();
            } else {
                System.out.println("Non-command here!");
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
