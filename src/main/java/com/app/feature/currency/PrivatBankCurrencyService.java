package com.app.feature.currency;

import com.app.feature.api.CurrencyJsonUpdate;
import com.app.feature.api.Utilities;
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

public class PrivatBankCurrencyService implements CurrencyService {
    @Override
    public Map<String, Double> getRate(List<Currency> currencies) {
        String url = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=11";

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
            throw new IllegalStateException("Can't connect to PrivatBank API");
        }


            //normalize PrivatBank currencies - obsolete RUR values replace with actual RUB value
            json = json.replaceAll("RUR", "RUB");

            //convert JSON to Java objects
            Type typeToken = TypeToken
                    .getParameterized(List.class, CurrencyItemPrivat.class)
                    .getType();
            List<CurrencyItemPrivat> receivedApiCurrencies = new Gson().fromJson(json, typeToken);


            //find rates
            Map<String, Double> rate = new HashMap<>();
            for (Currency currency : currencies) {
                double privatBuy = receivedApiCurrencies.stream()
                        .filter(it -> it.getTypeOfChangeCurrency() == currency)
                        .map(CurrencyItem::getBuyRate)
                        .findFirst().orElse(-1f);

                double privatSale = receivedApiCurrencies.stream()
                        .filter(it -> it.getTypeOfChangeCurrency() == currency)
                        .map(CurrencyItem::getSellRate)
                        .findFirst().orElse(-1f);

                rate.put("buy" + currency, privatBuy);
                rate.put("sell" + currency, privatSale);
            }
            return rate;
        }


    @Data
    public static class CurrencyItemPrivat implements CurrencyItem {
        private Currency ccy;
        private Currency base_ccy;
        private float buy;
        private float sale;

        @Override
        public Currency getTypeOfChangeCurrency() {
            return ccy;
        }

        @Override
        public Currency getTypeOfBaseCurrency() {
            return base_ccy;
        }

        @Override
        public float getBuyRate() {
            return buy;
        }

        @Override
        public float getSellRate() {
            return sale;
        }

        @Override
        public float getRate() {
            return -1;
        }
    }

}


