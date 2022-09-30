package com.app.feature.currency;

import com.app.feature.currency.dto.Currency;

import java.util.Map;

public interface CurrencyService {
    Map<String, Double> getRate(Currency currency);
}
