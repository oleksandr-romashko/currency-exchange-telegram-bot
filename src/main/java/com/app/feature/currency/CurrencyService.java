package com.app.feature.currency;

import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItem;

import java.util.List;

public interface CurrencyService {
    List<CurrencyItem> getRate(List<Currency> currencies);
}
