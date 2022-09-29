package com.app.feature.currency.dto;

public enum Bank {
    NBU("National Bank of Ukraine"),
    PrivatBank("PrivatBank"),
    Monobank("Monobank");

    private final String fullName;
    Bank(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}
