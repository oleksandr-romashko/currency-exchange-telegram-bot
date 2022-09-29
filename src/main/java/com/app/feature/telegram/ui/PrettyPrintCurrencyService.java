package com.app.feature.telegram.ui;

import com.app.feature.api.dto.AbstractCurrencyItem;
import com.vdurmont.emoji.EmojiParser;

import java.util.List;

public class PrettyPrintCurrencyService {
    private final String euro_emoji = EmojiParser.parseToUnicode(":eu:");
    private final String dollar_emoji = EmojiParser.parseToUnicode(":us:");
    private final String rub_emoji = EmojiParser.parseToUnicode(":ru:");
    private final String ua_emoji = EmojiParser.parseToUnicode(":ua:");

    public String convert(List<AbstractCurrencyItem> currencyItemPrivatList, int rounding) {
        StringBuilder res = new StringBuilder("Exchange rate in PrivatBank:");
        for(AbstractCurrencyItem currencyItemPrivat : currencyItemPrivatList) {
            String currencyFirstFlag = getCurrencyFlag(currencyItemPrivat.getCurrency().name());
            String currencySecondFlag = getCurrencyFlag(currencyItemPrivat.getBaseCurrency().name());

            float roundedBuyRate = Math.round(currencyItemPrivat.getBuy() * Math.pow(10, rounding))/(float)Math.pow(10, rounding);
            float roundedSaleRate = Math.round(currencyItemPrivat.getSale() * Math.pow(10, rounding))/(float)Math.pow(10, rounding);

            res.append(System.lineSeparator())
                    .append(" ".repeat(4))
                    .append(currencyItemPrivat.getBaseCurrency())
                    .append("/")
                    .append(currencyItemPrivat.getCurrency().name())
                    .append(" ".repeat(3))
                    .append(currencySecondFlag)
                    .append("/")
                    .append(currencyFirstFlag)
                    .append(System.lineSeparator())
                    .append(" ".repeat(8))
                    .append("Buying: ")
                    .append(roundedBuyRate)
                    .append(" / ")
                    .append("Selling: ")
                    .append(roundedSaleRate);
        }
        return res.toString();
    }

    private String getCurrencyFlag(String currencyName) {
        return switch (currencyName) {
            case "EUR" -> euro_emoji;
            case "USD" -> dollar_emoji;
            case "RUB" -> rub_emoji;
            case "UAH" -> ua_emoji;
            default -> null;
        };
    }
}
