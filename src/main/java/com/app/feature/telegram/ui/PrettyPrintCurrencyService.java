package com.app.feature.telegram.ui;

import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItem;
import com.vdurmont.emoji.EmojiParser;

import java.util.List;

public class PrettyPrintCurrencyService {
    private final String euro_emoji = EmojiParser.parseToUnicode(":eu:");
    private final String dollar_emoji = EmojiParser.parseToUnicode(":us:");
    private final String bitcoin_emoji = EmojiParser.parseToUnicode(":moneybag:");
    private final String rur_emoji = EmojiParser.parseToUnicode(":ru:");
    private final String ua_emoji = EmojiParser.parseToUnicode(":ua:");

    public String convert(List<CurrencyItem> currencyItemPrivatList, int rounding) {
        StringBuilder res = new StringBuilder("Exchange rate in PrivatBank:");
        for(CurrencyItem currencyItemPrivat : currencyItemPrivatList) {
            String currencyOneFlag = getCurrencyFlag(currencyItemPrivat.getCcy().name());
            String currencyTwoFlag = getCurrencyFlag(currencyItemPrivat.getBase_ccy().name());

            float roundedBuyRate = Math.round(currencyItemPrivat.getBuy() * Math.pow(10, rounding))/(float)Math.pow(10, rounding);
            float roundedSaleRate = Math.round(currencyItemPrivat.getSale() * Math.pow(10, rounding))/(float)Math.pow(10, rounding);

            res.append(System.lineSeparator().repeat(2)).append(currencyItemPrivat.getBase_ccy())
                    .append("/").append(currencyItemPrivat.getCcy().name())
                    .append("   ").append(currencyTwoFlag).append("/").append(currencyOneFlag)
                    .append(System.lineSeparator()).append("Buying: ").append(roundedBuyRate)
                    .append(" / ").append("Selling: ").append(roundedSaleRate);
        }
        return res.toString();
    }

    public String getCurrencyFlag(String currencyName) {
        switch (currencyName) {
            case "UAH":
                return ua_emoji;
            case "BTC":
                return bitcoin_emoji;
            case "EUR":
                return euro_emoji;
            case "USD":
                return dollar_emoji;
            case "RUR":
                return rur_emoji;
        }

        return null;
    }
}
