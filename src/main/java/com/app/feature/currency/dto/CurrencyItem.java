package com.app.feature.currency.dto;

public interface CurrencyItem {
    Currency getTypeOfChangeCurrency();
    Currency getTypeOfBaseCurrency();
    float getBuyRate();
    float getSellRate();
    float getRate();
}
