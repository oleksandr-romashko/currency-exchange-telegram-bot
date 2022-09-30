package com.app.feature.currency;

import com.app.feature.currency.dto.Currency;
import com.app.feature.currency.dto.CurrencyItemPrivat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrivatBankCurrencyService implements CurrencyService {
    @Override
    public Map<String, Double> getRate(Currency currency) {
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


        //find rate
        double privatBuy = receivedApiCurrencies.stream()
                .filter(it -> it.getCcy() == currency)
                .map(CurrencyItemPrivat::getBuy)
                .collect(Collectors.toList()).get(0);

        double privatSale = receivedApiCurrencies.stream()
                .filter(it -> it.getCcy() == currency)
                .map(CurrencyItemPrivat::getSale)
                .collect(Collectors.toList()).get(0);

        Map<String, Double> rate = new HashMap<>();
        rate.put("buy" + currency, privatBuy);
        rate.put("sell" + currency, privatSale);

        return rate;
    }
}
