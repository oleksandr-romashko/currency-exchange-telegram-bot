package com.app.feature.telegram;

import com.app.feature.currency.CurrencyService;
import com.app.feature.currency.PrivatBankCurrencyService;
import com.app.feature.currency.dto.Bank;
import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItem;
import com.app.feature.telegram.command.*;
import com.app.feature.telegram.ui.PrettyPrintCurrencyService;
import com.app.feature.user.UserUtil;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CurrencyTelegramBot extends TelegramLongPollingCommandBot {
    private final CurrencyService currencyService;
    private final PrettyPrintCurrencyService prettyPrintCurrencyService;
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

    private InlineKeyboardMarkup createKeyboard(List<List<InlineKeyboardButton>> rowList) {
        return InlineKeyboardMarkup
                .builder()
                .keyboard(rowList)
                .build();
    }

    private InlineKeyboardButton createButton(String text, String callback) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callback)
                .build();
    }

    private void createBackToSettingsButton(List<List<InlineKeyboardButton>> rowList) {
        rowList.add(List.of(createButton("Back to settings", "back_to_settings")));
    }

    private void onGetInfoPressed() {
        System.out.println("Get_info pressed!");

        int rounding = userUtil.getRoundingByUserId(chatId);
        List<Currency> savedCurrency = userUtil.getCurrencyTypeByUserId(chatId);
        System.out.println("savedCurrency = " + savedCurrency);
        List<CurrencyItem> currencyRateListPrivat = currencyService.getRate(savedCurrency);
        System.out.println("currencyRateListPrivat = " + currencyRateListPrivat);

        String prettyText = prettyPrintCurrencyService.convert(currencyRateListPrivat, rounding);
        onBackToMenuPressed(prettyText);
    }

    private void onSettingsPressed() {
        System.out.println("Settings pressed!");

        InlineKeyboardButton decimalPlacesButton = InlineKeyboardButton.builder().text("Number of decimal places").callbackData("number_of_decimal_places").build();
        InlineKeyboardButton currencyButton = InlineKeyboardButton.builder().text("Currency").callbackData("currency").build();
        InlineKeyboardButton notificationTimeButton = InlineKeyboardButton.builder().text("Notification time").callbackData("notification_time").build();
        InlineKeyboardButton bankButton = InlineKeyboardButton.builder().text("Bank").callbackData("bank").build();
        InlineKeyboardButton getInfoButton = InlineKeyboardButton.builder().text("Back to main menu").callbackData("main_menu").build();
        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>(List.of(decimalPlacesButton));
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>(List.of(currencyButton));
        List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>(List.of(notificationTimeButton));
        List<InlineKeyboardButton> keyboardRow4 = new ArrayList<>(List.of(bankButton));
        List<InlineKeyboardButton> keyboardRow5 = new ArrayList<>(List.of(getInfoButton));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>(List.of(keyboardRow1, keyboardRow2, keyboardRow3, keyboardRow4, keyboardRow5));

        sendMessageWithKeyboard("Settings:", chatId.toString(), createKeyboard(rowList));
    }

    private void onBackToMenuPressed(String text) {
        InlineKeyboardButton getInfoButton = InlineKeyboardButton.builder().text("Get info").callbackData("get_info").build();
        InlineKeyboardButton settingsButton = InlineKeyboardButton.builder().text("Settings").callbackData("settings").build();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>(List.of(List.of(getInfoButton), List.of(settingsButton)));
        sendMessageWithKeyboard(text, chatId.toString(), createKeyboard(rowList));
    }

    private void onDecimalPlacesPressed() {
        System.out.println("Decimal_places setting pressed!");

        String text = "Select the number of decimal places for the exchange rate to be displayed (the number will be rounded):";
        Integer savedRounding = userUtil.getRoundingByUserId(chatId);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (int i = 2; i < 5; i++) {
            rowList.add(List.of(createButton(getRoundingButton(savedRounding, i), i + ":_decimal_places")));
        }
        createBackToSettingsButton(rowList);

        sendMessageWithKeyboard(text, chatId.toString(), createKeyboard(rowList));
    }

    private void onDecimalTypePressed(String data, Integer messageId) throws TelegramApiException {
        String[] param = data.split(":");
        Integer newNumber = Integer.valueOf(param[0]);
        System.out.println(newNumber + " pressed!");
        Integer savedRounding = userUtil.getRoundingByUserId(chatId);
        if(!newNumber.equals(savedRounding)) {
            userUtil.setRoundingByUserId(chatId, newNumber);
            savedRounding = userUtil.getRoundingByUserId(chatId);

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            for (int i = 2; i < 5; i++) {
                rowList.add(List.of(createButton(getRoundingButton(savedRounding, i), i + ":_decimal_places")));
            }
            createBackToSettingsButton(rowList);

            editMessageWithKeyboard(messageId, chatId.toString(), createKeyboard(rowList));
        }

    }

    private void onCurrencyPressed() {
        System.out.println("Currency setting pressed!");

        String text = "Select the currency to be displayed:";
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<Currency> savedCurrency = userUtil.getCurrencyTypeByUserId(chatId);
        for (Currency currency : Arrays.stream(Currency.values()).limit(3).collect(Collectors.toList())) {
            rowList.add(List.of(createButton(getCurrencyButton(savedCurrency, currency), "Currency:" + currency)));
        }
        createBackToSettingsButton(rowList);

        sendMessageWithKeyboard(text, chatId.toString(), createKeyboard(rowList));
    }

    private void onCurrencyTypePressed(String data, Integer messageId) throws TelegramApiException {
        String[] param = data.split(":");
        Currency newCurrency = Currency.valueOf(param[1]);
        System.out.println(newCurrency + " pressed!");
        userUtil.setCurrencyTypeByUserId(chatId, newCurrency);
        List<Currency> savedCurrency = userUtil.getCurrencyTypeByUserId(chatId);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Currency currency : Arrays.stream(Currency.values()).limit(3).collect(Collectors.toList())) {
            rowList.add(List.of(createButton(getCurrencyButton(savedCurrency, currency), "Currency:" + currency)));
        }
        createBackToSettingsButton(rowList);

        editMessageWithKeyboard(messageId, chatId.toString(), createKeyboard(rowList));
    }

    private void onNotificationTimePressed() {
        System.out.println("Notification_time setting pressed!");

        String text = "Select daily notification time or turn it off:";
        String savedAlarmTime = userUtil.getAlarmTimeByUserId(chatId);
        InlineKeyboardButton turnOffNotificationsButton = InlineKeyboardButton.builder().text(getAlarmTimeButton(savedAlarmTime, "Turn off")).callbackData("Turn off:_alarm_time").build();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (int i = 9; i < 19; i++) {
            buttons.add(createButton(getAlarmTimeButton(savedAlarmTime, String.valueOf(i)), i + ":_alarm_time"));
        }
        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>(List.of(buttons.get(0), buttons.get(1), buttons.get(2)));
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>(List.of(buttons.get(3), buttons.get(4), buttons.get(5)));
        List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>(List.of(buttons.get(6), buttons.get(7), buttons.get(8)));
        List<InlineKeyboardButton> keyboardRow4 = new ArrayList<>(List.of(buttons.get(9), turnOffNotificationsButton));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>(List.of(keyboardRow1, keyboardRow2, keyboardRow3, keyboardRow4));
        createBackToSettingsButton(rowList);

        sendMessageWithKeyboard(text, chatId.toString(), createKeyboard(rowList));
    }

    private void onNotificationTimeTypePressed(String data, Integer messageId) throws TelegramApiException {
        String[] param = data.split(":");
        String newAlarmTime = param[0];
        String savedAlarmTime = userUtil.getAlarmTimeByUserId(chatId);
        System.out.println(newAlarmTime + " pressed!");
        if(!Objects.equals(newAlarmTime, savedAlarmTime)) {
            userUtil.setAlarmTimeByUserId(chatId, newAlarmTime);
            savedAlarmTime = userUtil.getAlarmTimeByUserId(chatId);

            InlineKeyboardButton turnOffNotificationsButton = InlineKeyboardButton.builder().text(getAlarmTimeButton(savedAlarmTime, "Turn off")).callbackData("Turn off:_alarm_time").build();
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (int i = 9; i < 19; i++) {
                buttons.add(createButton(getAlarmTimeButton(savedAlarmTime, String.valueOf(i)), i + ":_alarm_time"));
            }
            List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>(List.of(buttons.get(0), buttons.get(1), buttons.get(2)));
            List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>(List.of(buttons.get(3), buttons.get(4), buttons.get(5)));
            List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>(List.of(buttons.get(6), buttons.get(7), buttons.get(8)));
            List<InlineKeyboardButton> keyboardRow4 = new ArrayList<>(List.of(buttons.get(9), turnOffNotificationsButton));
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>(List.of(keyboardRow1, keyboardRow2, keyboardRow3, keyboardRow4));
            createBackToSettingsButton(rowList);

            editMessageWithKeyboard(messageId, chatId.toString(), createKeyboard(rowList));
        }
    }

    private void onBankPressed() {
        System.out.println("Bank setting pressed!");

        String text = "Select the bank to be displayed:";
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<Bank> savedBanks = userUtil.getBankTypeByUserId(chatId);
        for (Bank bank : Bank.values()) {
            rowList.add(List.of(createButton(getBankButton(savedBanks, bank), "Bank:" + bank)));
        }
        createBackToSettingsButton(rowList);

        sendMessageWithKeyboard(text, chatId.toString(), createKeyboard(rowList));
    }

    private void onBankTypePressed(String data, Integer messageId) throws TelegramApiException {
        String[] param = data.split(":");
        Bank newBank = Bank.valueOf(param[1]);
        System.out.println(newBank + " pressed!");
        userUtil.setBankTypeByUserId(chatId, newBank);
        List<Bank> savedBanks = userUtil.getBankTypeByUserId(chatId);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Bank bank : Bank.values()) {
            rowList.add(List.of(createButton(getBankButton(savedBanks, bank), "Bank:" + bank)));
        }
        createBackToSettingsButton(rowList);

        editMessageWithKeyboard(messageId, chatId.toString(), createKeyboard(rowList));
    }

    private String getCurrencyButton(List<Currency> saved, Currency current) {
        return saved.stream().filter(currency -> currency.equals(current)).findFirst().isEmpty() ? current.name() : current + EmojiParser.parseToUnicode(":white_check_mark:");
    }

    private String getBankButton(List<Bank> saved, Bank bank) {
        return saved.stream().filter(it -> it.equals(bank)).findFirst().isEmpty() ? bank.name() : bank + EmojiParser.parseToUnicode(":white_check_mark:");
    }

    private String getRoundingButton(Integer saved, Integer number) {
        return Objects.equals(saved, number) ? number + EmojiParser.parseToUnicode(":white_check_mark:") : number.toString();
    }

    private String getAlarmTimeButton(String saved, String param) {
        return Objects.equals(saved, param) ? param + EmojiParser.parseToUnicode(":white_check_mark:") : param;
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
            } else if (update.getCallbackQuery().getData().equals("number_of_decimal_places")) {
                onDecimalPlacesPressed();
            } else if (update.getCallbackQuery().getData().equals("notification_time")) {
                onNotificationTimePressed();
            } else if (update.getCallbackQuery().getData().equals("currency")) {
                onCurrencyPressed();
            } else if (update.getCallbackQuery().getData().equals("bank")) {
                onBankPressed();
            } else if (update.getCallbackQuery().getData().equals("main_menu")) {
                onBackToMenuPressed("Main menu:");
            } else if (update.getCallbackQuery().getData().equals("back_to_settings")) {
                onSettingsPressed();
            } else if (update.getCallbackQuery().getData().contains("Currency:")) {
                try {
                    onCurrencyTypePressed(update.getCallbackQuery().getData(), messageId);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (update.getCallbackQuery().getData().contains("Bank:")) {
                try {
                    onBankTypePressed(update.getCallbackQuery().getData(), messageId);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (update.getCallbackQuery().getData().contains(":_decimal_places")) {
                try {
                    onDecimalTypePressed(update.getCallbackQuery().getData(), messageId);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (update.getCallbackQuery().getData().contains(":_alarm_time")) {
                try {
                    onNotificationTimeTypePressed(update.getCallbackQuery().getData(), messageId);
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
