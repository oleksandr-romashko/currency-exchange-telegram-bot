package com.app.feature.currency;

import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItemMono;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonobankCurrencyService implements CurrencyService {
    @Override
    public Map<String, Double> getRate(List<Currency> currencies) {
        String url = "https://api.monobank.ua/bank/currency";

        //get JSON from bank API
        String json;
        try {
            json = Jsoup
                    .connect(url)
                    .ignoreContentType(true)
                    .get()
                    .body()
                    .text();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Can't connect to Monobank API");
        }

        //replace for enum Currency
        json = json
                .replace(":980", ":UAH")
                .replace(":978", ":EUR")
                .replace(":840", ":USD")
                .replace(":643", ":RUB")
                .replace(":826", ":GBP");

        //convert JSON to Java objects
        Type typeToken = TypeToken
                .getParameterized(List.class, CurrencyItemMono.class)
                .getType();
        List<CurrencyItemMono> currencyItemMono = new Gson().fromJson(json, typeToken);


        //find rates
        Map<String, Double> rate = new HashMap<>();
        for (Currency currency: currencies) {
            if (currency == Currency.GBP) {
                double monoCrossCurseGBP = currencyItemMono.stream()
                        .filter(it -> it.getCurrencyCodeA() == currency)
                        .map(CurrencyItemMono::getRateCross)
                        .findFirst().orElse(-1f);


                rate.put("rate" + currency, monoCrossCurseGBP);
            } else {
                double monoBuy = currencyItemMono.stream()
                        .filter(it -> it.getCurrencyCodeA() == currency)
                        .map(CurrencyItemMono::getRateBuy)
                        .findFirst().orElse(-1f);

                double monoSell = currencyItemMono.stream()
                        .filter(it -> it.getCurrencyCodeA() == currency)
                        .map(CurrencyItemMono::getRateSell)
                        .findFirst().orElse(-1f);

                rate.put("buy" + currency, monoBuy);
                rate.put("sell" + currency, monoSell);
            }
        }
            return rate;
    }
}
