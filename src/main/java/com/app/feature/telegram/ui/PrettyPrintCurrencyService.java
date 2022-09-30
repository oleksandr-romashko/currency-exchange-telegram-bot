package com.app.feature.telegram.ui;

import com.app.feature.currency.dto.Currency;
import com.vdurmont.emoji.EmojiParser;

import java.util.Map;

public class PrettyPrintCurrencyService {
    private final String euro_emoji = EmojiParser.parseToUnicode(":eu:");
    private final String dollar_emoji = EmojiParser.parseToUnicode(":us:");
    private final String bitcoin_emoji = EmojiParser.parseToUnicode(":moneybag:");
    private final String rub_emoji = EmojiParser.parseToUnicode(":ru:");
    private final String ua_emoji = EmojiParser.parseToUnicode(":ua:");
    private final String gb_emoji = EmojiParser.parseToUnicode(":gb:");

    public String convert(Map<String, Double> currencyRate, Currency currency, int rounding) {
        StringBuilder res = new StringBuilder();
        String currencyFirstFlag = getCurrencyFlag(Currency.UAH.name());
        String currencySecondFlag = getCurrencyFlag(currency.name());

        if(currencyRate.containsKey("rate" + currency)){
            float roundedRate = Math.round(currencyRate.get("rate" + currency) * Math.pow(10, rounding)) / (float) Math.pow(10, rounding);
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
            return res.toString();
        }
        float roundedBuyRate = Math.round(currencyRate.get("buy" + currency) * Math.pow(10, rounding)) / (float) Math.pow(10, rounding);
        float roundedSaleRate = Math.round(currencyRate.get("sell" + currency) * Math.pow(10, rounding)) / (float) Math.pow(10, rounding);

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

        return res.toString();
    }

    public String convertNotSupportedCurrency(String bank, Currency currency) {
        StringBuilder res = new StringBuilder();
        String currencyFirstFlag = getCurrencyFlag(Currency.UAH.name());
        String currencySecondFlag = getCurrencyFlag(currency.name());

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
                .append("Sorry, ")
                .append(bank)
                .append(" doesn't support this specific currency - ")
                .append(currency.name())
                .append("!");

        return res.toString();
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
