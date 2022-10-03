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

public class MonobankCurrencyService implements CurrencyService {

    @Override
    public List<AbstractCurrencyItem> getRates(List<Currency> savedCurrency) {
        //get JSON from file
        String json = new CurrencyFileReaderService().getRatesForMonobank();

        //normalize JSON by replacing ISO currency codes with Currency enum names
        String replaceJson = json
                .replace(":978", ":EUR")
                .replace(":840", ":USD")
                .replace(":643", ":RUB")
                .replace(":980", ":UAH");

        //Convert JSON to Java objects
        Type typeToken = TypeToken
                .getParameterized(List.class, CurrencyItemMonobank.class)
                .getType();
        List<CurrencyItemMonobank> receivedApiCurrencies = new Gson().fromJson(replaceJson, typeToken);

        //find CurrencyItem
        List<AbstractCurrencyItem> resultList = new ArrayList<>();
        for(Currency currency : savedCurrency) {
            AbstractCurrencyItem currencyItem = receivedApiCurrencies
                    .stream()
                    .filter(it -> it.currencyCodeB.equals(Currency.UAH))
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
     *       "currencyCodeA":978,
     *       "currencyCodeB":980,
     *       "date":1664399409,
     *       "rateBuy":35.45,
     *       "rateSell":37.4504
     *      ("rateCross":x.xxxx)  //is not present for some currencies
     *   },
     *   ...,
     *   {}
     * ]
     */
    public static class CurrencyItemMonobank extends AbstractCurrencyItem {
        @Setter
        private String currencyCodeA;
        @Getter
        @Setter
        private Currency currencyCodeB;
        @Getter
        @Setter
        private int date;
        @Getter
        @Setter
        private float rateBuy;
        @Getter
        @Setter
        private float rateSell;
        @Getter
        @Setter
        private float rateCross;

        @Override
        public String getCurrency() {
            return currencyCodeA;
        }

        @Override
        public Currency getBaseCurrency() {
            return currencyCodeB;
        }

        @Override
        public float getBuy() {
            return rateBuy;
        }

        @Override
        public float getSale() {
            return rateSell;
        }
    }
}
