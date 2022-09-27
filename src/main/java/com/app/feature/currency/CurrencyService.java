package com.app.feature.currency;

import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItem;

public interface CurrencyService {
    CurrencyItem getRate(Currency currency);
}
