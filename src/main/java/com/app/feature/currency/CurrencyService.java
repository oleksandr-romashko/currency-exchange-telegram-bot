package com.app.feature.currency;

import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItem;

import java.io.IOException;

public interface CurrencyService {
    CurrencyItem getRate(Currency currency) throws IOException;
}
