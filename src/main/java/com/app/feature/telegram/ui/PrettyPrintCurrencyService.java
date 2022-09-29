package com.app.feature.telegram.ui;

import com.app.feature.currency.dto.CurrencyItem;
import com.vdurmont.emoji.EmojiParser;

import java.util.List;

public class PrettyPrintCurrencyService {
    private final String euro_emoji = EmojiParser.parseToUnicode(":eu:");
    private final String dollar_emoji = EmojiParser.parseToUnicode(":us:");
    private final String bitcoin_emoji = EmojiParser.parseToUnicode(":moneybag:");
    private final String rub_emoji = EmojiParser.parseToUnicode(":ru:");
    private final String ua_emoji = EmojiParser.parseToUnicode(":ua:");

    public String convert(List<CurrencyItem> currencyItemPrivatList, int rounding) {
        StringBuilder res = new StringBuilder("Exchange rate in PrivatBank:");
        for(CurrencyItem currencyItemPrivat : currencyItemPrivatList) {
            String currencyFirstFlag = getCurrencyFlag(currencyItemPrivat.getCcy().name());
            String currencySecondFlag = getCurrencyFlag(currencyItemPrivat.getBase_ccy().name());

            float roundedBuyRate = Math.round(currencyItemPrivat.getBuy() * Math.pow(10, rounding))/(float)Math.pow(10, rounding);
            float roundedSaleRate = Math.round(currencyItemPrivat.getSale() * Math.pow(10, rounding))/(float)Math.pow(10, rounding);

            res.append(System.lineSeparator())
                    .append(" ".repeat(4))
                    .append(currencyItemPrivat.getBase_ccy())
                    .append("/")
                    .append(currencyItemPrivat.getCcy().name())
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

    public String getCurrencyFlag(String currencyName) {
        return switch (currencyName) {
            case "UAH" -> ua_emoji;
            case "BTC" -> bitcoin_emoji;
            case "EUR" -> euro_emoji;
            case "USD" -> dollar_emoji;
            case "RUB" -> rub_emoji;
            default -> null;
        };
    }
}
