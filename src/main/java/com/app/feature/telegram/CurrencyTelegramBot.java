package com.app.feature.telegram;

import com.app.feature.currency.dto.Bank;
import com.app.feature.currency.dto.Currency;
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

import java.util.*;
import java.util.stream.Collectors;

public class CurrencyTelegramBot extends TelegramLongPollingCommandBot {
    private final PrettyPrintCurrencyService prettyPrintCurrencyService;
    private final UserUtil userUtil = new UserUtil();
    private Long chatId;

    public CurrencyTelegramBot() {
        prettyPrintCurrencyService = new PrettyPrintCurrencyService();

        register(new StartCommand());
        register(new GetInfoCommand());
        register(new SettingsCommand());
    }

    public CurrencyTelegramBot(Long chatId) {
        prettyPrintCurrencyService = new PrettyPrintCurrencyService();
        this.chatId = chatId;

        register(new StartCommand());
        register(new GetInfoCommand());
        register(new SettingsCommand());
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

    private void makeFinalText(StringBuilder text, Bank bank, List<Currency> savedCurrency, int rounding) {
        text.append("Exchange rate in ").append(bank.getFullName()).append(":");
        Map<String, Double> currencyRates = bank.getCurrencyService().getRate(savedCurrency);
        text.append(prettyPrintCurrencyService.convert(currencyRates, savedCurrency, rounding, bank))
                .append(System.lineSeparator().repeat(2));
    }

    public void onGetInfoPressed() {
        System.out.println("Get_info pressed!");

        int rounding = userUtil.getRoundingByUserId(chatId);
        List<Currency> savedCurrency = userUtil.getCurrencyTypeByUserId(chatId);
        List<Bank> savedBanks = userUtil.getBankTypeByUserId(chatId);
        StringBuilder text = new StringBuilder();
        for (Bank bank : savedBanks) {
            makeFinalText(text, bank, savedCurrency, rounding);
        }
        onBackToMenuPressed(text.toString());
    }

    public void onSettingsPressed() {
        System.out.println("Settings pressed!");

        List<InlineKeyboardButton> keyboardRow1 = new ArrayList<>(List.of(createButton("Number of decimal places", "number_of_decimal_places")));
        List<InlineKeyboardButton> keyboardRow2 = new ArrayList<>(List.of(createButton("Currency", "currency")));
        List<InlineKeyboardButton> keyboardRow3 = new ArrayList<>(List.of(createButton("Notification time", "notification_time")));
        List<InlineKeyboardButton> keyboardRow4 = new ArrayList<>(List.of(createButton("Bank", "bank")));
        List<InlineKeyboardButton> keyboardRow5 = new ArrayList<>(List.of(createButton("Back to main menu", "main_menu")));
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>(List.of(keyboardRow1, keyboardRow2, keyboardRow3, keyboardRow4, keyboardRow5));

        sendMessageWithKeyboard("Settings:", chatId.toString(), createKeyboard(rowList));
    }

    private void onBackToMenuPressed(String text) {
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>(List.of(
                List.of(createButton("Get info", "get_info")),
                List.of(createButton("Settings", "settings"))
        ));
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
        System.out.println(newNumber + " decimal places option pressed!");
        Integer savedRounding = userUtil.getRoundingByUserId(chatId);
        if (!newNumber.equals(savedRounding)) {
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
        List<Currency> savedCurrencies = userUtil.getCurrencyTypeByUserId(chatId);
        for (Currency currency : Arrays.stream(Currency.values()).filter(it -> it != Currency.UAH).collect(Collectors.toList())) {
            rowList.add(List.of(createButton(getCurrencyButton(savedCurrencies, currency), "Currency:" + currency)));
        }
        createBackToSettingsButton(rowList);

        sendMessageWithKeyboard(text, chatId.toString(), createKeyboard(rowList));
    }

    private void onCurrencyTypePressed(String data, Integer messageId) throws TelegramApiException {
        String[] param = data.split(":");
        Currency newCurrency = Currency.valueOf(param[1]);
        System.out.println(newCurrency + " currency option pressed!");
        userUtil.setCurrencyTypeByUserId(chatId, newCurrency);
        List<Currency> savedCurrency = userUtil.getCurrencyTypeByUserId(chatId);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Currency currency : Arrays.stream(Currency.values()).filter(it -> it != Currency.UAH).collect(Collectors.toList())) {
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
        System.out.println(newAlarmTime + " notification option pressed!");
        if (!Objects.equals(newAlarmTime, savedAlarmTime)) {
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
        System.out.println(newBank.getFullName() + " bank option pressed!");
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
        return saved.stream().filter(it -> it.equals(bank)).findFirst().isEmpty() ? bank.getFullName() : bank.getFullName() + EmojiParser.parseToUnicode(":white_check_mark:");
    }

    private String getRoundingButton(Integer saved, Integer number) {
        return Objects.equals(saved, number) ? number + EmojiParser.parseToUnicode(":white_check_mark:") : number.toString();
    }

    private String getAlarmTimeButton(String saved, String param) {
        return Objects.equals(saved, param) ? param + EmojiParser.parseToUnicode(":white_check_mark:") : param;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
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
            System.out.println("Unsupported command or callback received");
//                    + " from User \"" + update.getMessage().getFrom().getUserName() + "\""
//                    + " in chat with id \"" + update.getMessage().getChatId() + "\""
//                    + ", chat message is: \"" + update.getMessage().getText() + "\".");
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
