package com.app.feature.telegram;

import com.app.feature.currency.CurrencyService;
import com.app.feature.currency.PrivatBankCurrencyService;
import com.app.feature.currency.dto.Bank;
import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItem;
import com.app.feature.telegram.command.*;
import com.app.feature.telegram.ui.PrettyPrintCurrencyService;
import com.app.feature.user.UserInfo;
import com.app.feature.user.UserUtil;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CurrencyTelegramBot extends TelegramLongPollingCommandBot {
    private final CurrencyService currencyService;
    private final PrettyPrintCurrencyService prettyPrintCurrencyService;
//    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();
    private final UserUtil userUtil = new UserUtil();
    private Long chatId;

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

    private void editMessageWithKeyboard(Integer messageId, String chatId, InlineKeyboardMarkup keyboard) {
        EditMessageReplyMarkup message = new EditMessageReplyMarkup();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        if (keyboard != null) {
            message.setReplyMarkup(keyboard);
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup makeKeyboard(List<List<InlineKeyboardButton>> rowList) {
        return InlineKeyboardMarkup
                .builder()
                .keyboard(rowList)
                .build();
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

        InlineKeyboardButton getInfoButton = InlineKeyboardButton.builder().text("Get info").callbackData("get_info").build();
        InlineKeyboardButton settingsButton = InlineKeyboardButton.builder().text("Settings").callbackData("settings").build();
        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>(List.of(getInfoButton));
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>(List.of(settingsButton));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>(List.of(keyboardRow1, keyboardRow2));

        sendMessageWithKeyboard(prettyText, chatId.toString(), makeKeyboard(rowList));
    }

    private void onSettingsPressed() {
        System.out.println("Settings pressed!");

        InlineKeyboardButton decimalPlacesButton = InlineKeyboardButton.builder().text("Number of decimal places").callbackData("Number_of_decimal_places").build();
        InlineKeyboardButton currencyButton = InlineKeyboardButton.builder().text("Currency").callbackData("currency").build();
        InlineKeyboardButton notificationTimeButton = InlineKeyboardButton.builder().text("Notification time").callbackData("notification_time").build();
        InlineKeyboardButton bankButton = InlineKeyboardButton.builder().text("Bank").callbackData("bank").build();
        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>(List.of(decimalPlacesButton));
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>(List.of(currencyButton));
        List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>(List.of(notificationTimeButton));
        List<InlineKeyboardButton> keyboardRow4 = new ArrayList<>(List.of(bankButton));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>(List.of(keyboardRow1,keyboardRow2, keyboardRow3,keyboardRow4));

        sendMessageWithKeyboard("Settings", chatId.toString(), makeKeyboard(rowList));
    }

    private void onDecimalPlacesPressed() {
        System.out.println("Decimal_places pressed!");

        String text = "Select the number of decimal places:";
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int i = 2; i < 5; i++) {
            rowList.add(
                    List.of(
                            InlineKeyboardButton.builder()
                                    .text(String.valueOf(i))
                                    .callbackData(i + "_decimal_places")
                                    .build()));
        }

        sendMessageWithKeyboard(text, chatId.toString(), makeKeyboard(rowList));
    }

    private void onCurrencyPressed() {
        System.out.println("Currency pressed!");

        String text = "Select the currency/currencies:";

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<Currency> savedCurrency = userUtil.getCurrencyTypeByUserId(chatId);
        for (Currency currency : Arrays.stream(Currency.values()).limit(3).collect(Collectors.toList())) {
            rowList.add(
                    List.of(
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(savedCurrency, currency))
                                    .callbackData("Currency:" + currency)
                                    .build()));
        }

        sendMessageWithKeyboard(text, chatId.toString(), makeKeyboard(rowList));
    }

    private void onCurrencyTypePressed(String data, Integer messageId) throws TelegramApiException {
        String[] param = data.split(":");
        Currency newCurrency = Currency.valueOf(param[1]);
        System.out.println(newCurrency + " pressed!");
        userUtil.setCurrencyTypeByUserId(chatId, newCurrency);
        List<Currency> savedCurrency = userUtil.getCurrencyTypeByUserId(chatId);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Currency currency : Arrays.stream(Currency.values()).limit(3).collect(Collectors.toList())) {
            rowList.add(
                    List.of(
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(savedCurrency, currency))
                                    .callbackData("Currency:" + currency)
                                    .build()));
        }
        editMessageWithKeyboard(messageId, chatId.toString(), makeKeyboard(rowList));
    }

    private void onNotificationTimePressed() {
        System.out.println("Notification_time pressed!");

        String text = "Select notification time:";
        InlineKeyboardButton turnOffNotificationsButton = InlineKeyboardButton.builder().text("Turn off notifications").callbackData("turn_off_notifications").build();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (int i = 9; i < 19; i++) {
            buttons.add(InlineKeyboardButton.builder().text(String.valueOf(i))
                    .callbackData(String.valueOf(i)).build());
        }
        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>(List.of(buttons.get(0), buttons.get(1), buttons.get(2)));
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>(List.of(buttons.get(3), buttons.get(4), buttons.get(5)));
        List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>(List.of(buttons.get(6), buttons.get(7), buttons.get(8)));
        List<InlineKeyboardButton> keyboardRow4 = new ArrayList<>(List.of(buttons.get(9), turnOffNotificationsButton));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>(List.of(keyboardRow1, keyboardRow2, keyboardRow3, keyboardRow4));

        sendMessageWithKeyboard(text, chatId.toString(), makeKeyboard(rowList));
    }

    private void onBankPressed() {
        System.out.println("Bank pressed!");

        String text = "Select the bank/banks:";
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Bank bank : Bank.values()) {
            rowList.add(
                    List.of(
                            InlineKeyboardButton.builder()
                                    .text(bank.toString())
                                    .callbackData(bank.toString())
                                    .build()));
        }

        sendMessageWithKeyboard(text, chatId.toString(), makeKeyboard(rowList));
    }

    private boolean doesCurrencySaved(List<Currency> saved, Currency current) {
        return !saved.stream().filter(currency -> currency.equals(current)).findFirst().isEmpty();
    }

    private String getCurrencyButton(List<Currency> saved, Currency current) {
       return saved.stream().filter(currency -> currency.equals(current)).findFirst().isEmpty() ? current.name() : current + EmojiParser.parseToUnicode(":white_check_mark:");
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
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
            } else if (update.getCallbackQuery().getData().contains("Currency:")) {
                try {
                    onCurrencyTypePressed(update.getCallbackQuery().getData(), messageId);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Non-command here!");
            }
        } else
            System.out.println("Non-command here!");
    }

    @Override
    public String getBotUsername() {
        return new PropertiesConstants().propertiesReader("bot.name");
    }

    @Override
    public String getBotToken() {
        return new PropertiesConstants().propertiesReader("bot.token");
    }
}
