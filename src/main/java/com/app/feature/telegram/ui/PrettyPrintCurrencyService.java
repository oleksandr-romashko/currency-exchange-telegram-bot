package com.app.feature.telegram.ui;

import com.app.feature.currency.dto.Bank;
import com.app.feature.currency.dto.Currency;
import com.vdurmont.emoji.EmojiParser;

import java.util.List;
import java.util.Map;

public class PrettyPrintCurrencyService {
    private final String euro_emoji = EmojiParser.parseToUnicode(":eu:");
    private final String dollar_emoji = EmojiParser.parseToUnicode(":us:");
    private final String bitcoin_emoji = EmojiParser.parseToUnicode(":moneybag:");
    private final String rub_emoji = EmojiParser.parseToUnicode(":ru:");
    private final String ua_emoji = EmojiParser.parseToUnicode(":ua:");
    private final String gb_emoji = EmojiParser.parseToUnicode(":gb:");

    public String convert(Map<String, Double> currencyRates, List<Currency> currencies, int rounding, Bank bank) {
        StringBuilder res = new StringBuilder();
        for (Currency currency : currencies) {
            String currencyFirstFlag = getCurrencyFlag(Currency.UAH.name());
            String currencySecondFlag = getCurrencyFlag(currency.name());

            if (currencyRates.containsKey("rate" + currency)) {
                float roundedRate = Math.round(currencyRates.get("rate" + currency) * Math.pow(10, rounding)) / (float) Math.pow(10, rounding);
                res.append(System.lineSeparator())
                        .append(" ".repeat(4))
                        .append(Currency.UAH.name())
                        .append("/")
                        .append(currency.name())
                        .append(" ".repeat(3))
                        .append(currencyFirstFlag)
                        .append("/")
                        .append(currencySecondFlag)
                        .append(System.lineSeparator())
                        .append(" ".repeat(8))
                        .append("Rate: ")
                        .append(roundedRate);
            } else if(currencyRates.containsKey("buy" + currency)  && currencyRates.get("buy" + currency) == -1) {
                convertNotSupportedCurrency(bank.getFullName(), currency, res);
            } else {
                float roundedBuyRate = Math.round(currencyRates.get("buy" + currency) * Math.pow(10, rounding)) / (float) Math.pow(10, rounding);
                float roundedSaleRate = Math.round(currencyRates.get("sell" + currency) * Math.pow(10, rounding)) / (float) Math.pow(10, rounding);

                res.append(System.lineSeparator())
                        .append(" ".repeat(4))
                        .append(Currency.UAH.name())
                        .append("/")
                        .append(currency.name())
                        .append(" ".repeat(3))
                        .append(currencyFirstFlag)
                        .append("/")
                        .append(currencySecondFlag)
                        .append(System.lineSeparator())
                        .append(" ".repeat(8))
                        .append("Buying: ")
                        .append(roundedBuyRate)
                        .append(" / ")
                        .append("Selling: ")
                        .append(roundedSaleRate);
            }
        }
        return res.toString();
    }

    public void convertNotSupportedCurrency(String bank, Currency currency, StringBuilder text) {
        String currencyFirstFlag = getCurrencyFlag(Currency.UAH.name());
        String currencySecondFlag = getCurrencyFlag(currency.name());

        text.append(System.lineSeparator())
                .append(" ".repeat(4))
                .append(Currency.UAH.name())
                .append("/")
                .append(currency.name())
                .append(" ".repeat(3))
                .append(currencyFirstFlag)
                .append("/")
                .append(currencySecondFlag)
                .append(System.lineSeparator())
                .append(" ".repeat(8))
                .append("Sorry, ")
                .append(bank)
                .append(" doesn't support this specific currency - ")
                .append(currency.name())
                .append("!");
    }

    public String getCurrencyFlag(String currencyName) {
        return switch (currencyName) {
            case "UAH" -> ua_emoji;
            case "BTC" -> bitcoin_emoji;
            case "EUR" -> euro_emoji;
            case "USD" -> dollar_emoji;
            case "RUB" -> rub_emoji;
            case "GBP" -> gb_emoji;
            default -> null;
        };
    }
}
