package com.app.feature.api.service;

import com.app.feature.api.dto.Currency;

import java.io.IOException;
import java.util.Map;

public interface CurrencyService {
    Map<String, Double> getRate(Currency currency) throws IOException;
}

