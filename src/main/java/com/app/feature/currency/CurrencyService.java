package com.app.feature.currency;

import com.app.feature.currency.dto.Currency;

import java.util.List;
import java.util.Map;

public interface CurrencyService {
    Map<String, Double> getRate(List<Currency> currency);
}
