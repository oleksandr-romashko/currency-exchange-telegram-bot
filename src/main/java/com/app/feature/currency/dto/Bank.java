package com.app.feature.currency.dto;

public enum Bank {
    NBU("National Bank of Ukraine"),
    PrivatBank("PrivatBank"),
    Monobank("Monobank");

    Bank(String name) {
    }

    public String getName() {
        return name();
    }
}
