package com.app.feature.api.service;

import com.app.feature.api.CurrencyFileReaderService;
import com.app.feature.api.dto.AbstractCurrencyItem;
import com.app.feature.api.dto.Currency;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrivateBankCurrencyService implements CurrencyService {

    @Override
    public List<AbstractCurrencyItem> getRates(List<Currency> savedCurrency) {

        //get JSON from file
        String json = new CurrencyFileReaderService().getRatesForPrivatbank();

        //normalize Privatbank currencies - obsolete RUR values replace with actual RUB value
        json = json.replaceAll("RUR", "RUB");

        //convert JSON to Java objects
        Type typeToken = TypeToken
                .getParameterized(List.class, CurrencyItemPrivatbank.class)
                .getType();
        List<CurrencyItemPrivatbank> receivedApiCurrencies = new Gson()
                .fromJson(json, typeToken);

        //find CurrencyItem
        List<AbstractCurrencyItem> resultList = new ArrayList<>();
        for(Currency currency : savedCurrency) {
            AbstractCurrencyItem currencyItem = receivedApiCurrencies
                    .stream()
                    .filter(it -> it.getCurrency().equals(currency.name()))
                    .findFirst()
                    .orElseThrow();
            resultList.add(currencyItem);
        }
        return resultList;
    }

        /*
         * Example of reply JSON:
         * [
         *   {},
         *   ...
         *   ,{
         *       "ccy":"EUR",
         *       "base_ccy":"UAH",
         *       "buy":"34.97420",
         *       "sale":"37.45318"
         *   },
         *   ...,
         *   {}
         * ]
         */

        private static class CurrencyItemPrivatbank extends AbstractCurrencyItem {
            @Getter
            @Setter
            private String ccy;
            @Getter
            @Setter
            private Currency base_ccy;

            @Override
            public String getCurrency() {
                return ccy;
            }

            @Override
            public Currency getBaseCurrency() {
                return base_ccy;
            }
        }
    }
