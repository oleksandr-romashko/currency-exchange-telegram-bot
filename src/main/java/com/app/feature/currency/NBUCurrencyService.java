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

public class NBUCurrencyService implements CurrencyService{
    @Override
    public Map<String, Double> getRate(List<Currency> currencies) {
        String url = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

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
            throw new IllegalStateException("Can't connect to NBU API");
        }

        //convert JSON to Java objects
        Type typeToken = TypeToken
                .getParameterized(List.class, CurrencyItemNBU.class)
                .getType();
        List<CurrencyItemNBU> currencyItemsNBU = new Gson().fromJson(json, typeToken);

        //Find rates
        Map<String, Double> rate = new HashMap<>();
        for (Currency currency: currencies) {
            double currencyRate = currencyItemsNBU.stream()
                    .filter(it -> it.getTypeOfChangeCurrency() == currency)
                    .map(CurrencyItem::getRate)
                    .findFirst().orElse(-1f);

            rate.put("rate" + currency, currencyRate);
        }
        return rate;
    }

    @Data
    public static class CurrencyItemNBU implements CurrencyItem {
        private int r030;
        private String txt;
        private float rate;
        private Currency cc;
        private String exchangeDate;

        @Override
        public Currency getTypeOfChangeCurrency() {
            return cc;
        }

        @Override
        public Currency getTypeOfBaseCurrency() {
            return Currency.UAH;
        }

        @Override
        public float getBuyRate() {
            return -1;
        }

        @Override
        public float getSellRate() {
            return -1;
        }
    }
}
