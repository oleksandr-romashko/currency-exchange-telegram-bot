package com.app.feature.currency;

import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<CurrencyItemMono> currencyItemsMono = new Gson().fromJson(json, typeToken);


        //find rates
        Map<String, Double> rate = new HashMap<>();
        for (Currency currency: currencies) {
            if (currency == Currency.GBP) {
                double monoCrossCurseGBP = currencyItemsMono.stream()
                        .filter(it -> it.getTypeOfChangeCurrency() == currency)
                        .map(CurrencyItem::getRate)
                        .findFirst().orElse(-1f);


                rate.put("rate" + currency, monoCrossCurseGBP);
            } else {
                double monoBuy = currencyItemsMono.stream()
                        .filter(it -> it.getTypeOfChangeCurrency() == currency)
                        .map(CurrencyItem::getBuyRate)
                        .findFirst().orElse(-1f);

                double monoSell = currencyItemsMono.stream()
                        .filter(it -> it.getTypeOfChangeCurrency() == currency)
                        .map(CurrencyItem::getSellRate)
                        .findFirst().orElse(-1f);

                rate.put("buy" + currency, monoBuy);
                rate.put("sell" + currency, monoSell);
            }
        }
            return rate;
    }

    @Data
    public static class CurrencyItemMono implements CurrencyItem {
        private Currency currencyCodeA;
        private Currency currencyCodeB;
        private int date;
        private float rateBuy;
        private float rateSell;
        private float rateCross;

        @Override
        public Currency getTypeOfChangeCurrency() {
            return currencyCodeA;
        }

        @Override
        public Currency getTypeOfBaseCurrency() {
            return currencyCodeB;
        }

        @Override
        public float getBuyRate() {
            return rateBuy;
        }

        @Override
        public float getSellRate() {
            return rateSell;
        }

        @Override
        public float getRate() {
            return rateCross;
        }
    }
}
