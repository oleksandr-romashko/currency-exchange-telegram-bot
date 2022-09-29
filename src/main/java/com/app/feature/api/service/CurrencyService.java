package com.app.feature.api.service;

import com.app.feature.api.dto.AbstractCurrencyItem;
import com.app.feature.api.dto.Currency;

import java.util.List;

public interface CurrencyService {
    List<AbstractCurrencyItem> getRates(List<Currency> savedCurrency);
}

