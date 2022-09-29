package com.app.feature.api;

import java.io.IOException;

import org.jsoup.Jsoup;


public final class CurrencyUpdaterBankService {
    public static final String NBU_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    public static final String PRIVATBANK_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11";
    public static final String MONOBANK_URL = "https://api.monobank.ua/bank/currency";

    public String getRatesForNbu() throws IOException {
        return getRatesFromBankApi(NBU_URL);
    }

    public String getRatesForPrivatbank() throws IOException {
        return getRatesFromBankApi(PRIVATBANK_URL);
    }

    public String getRatesForMonobank() throws IOException {
        return getRatesFromBankApi(MONOBANK_URL);
    }

    private String getRatesFromBankApi(String url) throws IOException {
        return Jsoup
                .connect(url)
                .ignoreContentType(true)
                .get()
                .body()
                .text();
    }
}
