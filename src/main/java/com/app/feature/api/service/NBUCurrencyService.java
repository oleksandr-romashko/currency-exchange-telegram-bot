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

public class NBUCurrencyService implements CurrencyService {

    @Override
    public List<AbstractCurrencyItem> getRates(List<Currency> savedCurrency) {

        //get JSON from file
        String json = new CurrencyFileReaderService().getRatesForNbu();

        //convert JSON to Java objects
        Type typeToken = TypeToken
                .getParameterized(List.class, CurrencyItemNBU.class)
                .getType();
        List<CurrencyItemNBU> receivedApiCurrencies = new Gson()
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
     *     "r030":978,
     *     "txt":"Євро",
     *     "rate":34.9742,
     *     "cc":"EUR",
     *     "exchangedate":"29.09.2022"
     *   },
     *   ...,
     *   {}
     * ]
     */
    public static class CurrencyItemNBU extends AbstractCurrencyItem {
        @Getter
        @Setter
        private int r030;
        @Getter
        @Setter
        private String txt;
        @Setter
        private float rate;
        @Setter
        private String cc;
        @Getter
        @Setter
        private String exchangedate;

        @Override
        public String getCurrency() {
            return cc;
        }

        @Override
        public float getBuy() {
            return rate;
        }

        @Override
        public float getSale() {
            return 0;
        }

        @Override
        public Currency getBaseCurrency() {
            return Currency.UAH;
        }
    }
}