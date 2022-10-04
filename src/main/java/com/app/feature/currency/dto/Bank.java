package com.app.feature.currency.dto;

import com.app.feature.currency.CurrencyService;
import com.app.feature.currency.MonobankCurrencyService;
import com.app.feature.currency.NBUCurrencyService;
import com.app.feature.currency.PrivatBankCurrencyService;

public enum Bank {
    NBU("National Bank of Ukraine", new NBUCurrencyService()),
    PrivatBank("PrivatBank", new PrivatBankCurrencyService()),
    Monobank("Monobank", new MonobankCurrencyService());


    private final CurrencyService currencyService;
    private final String fullName;
    Bank(String fullName, CurrencyService currencyService) {
        this.currencyService = currencyService;
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public CurrencyService getCurrencyService() {
        return currencyService;
    }
}
