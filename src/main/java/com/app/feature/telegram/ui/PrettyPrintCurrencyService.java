package com.app.feature.telegram.ui;

import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItem;
import com.vdurmont.emoji.EmojiParser;

public class PrettyPrintCurrencyService {
    private String euro_emoji = EmojiParser.parseToUnicode(":eu:");
    private String dollar_emoji = EmojiParser.parseToUnicode(":us:");
    private String bitcoin_emoji = EmojiParser.parseToUnicode(":moneybag:");
    private String rur_emoji = EmojiParser.parseToUnicode(":ru:");
    private String ua_emoji = EmojiParser.parseToUnicode(":ua:");

    public String convert(CurrencyItem currencyItem, Currency currency) {
        String currencyOneFlag = getCurrencyFlag(currency.name());
        String currencyTwoFlag = getCurrencyFlag(currencyItem.getBase_ccy().name());

        float roundedBuyRate = Math.round(currencyItem.getBuy() * 100d)/100f;
        float roundedSaleRate = Math.round(currencyItem.getSale() * 100d)/100f;

        return  "Exchange rate in PrivatBank:" + System.lineSeparator() + currency.name() + "/" + currencyItem.getBase_ccy() + "   " + currencyOneFlag + "/"
                + currencyTwoFlag + System.lineSeparator() + "Buying: " + roundedBuyRate + " / " + "Selling: "
                + roundedSaleRate;
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
