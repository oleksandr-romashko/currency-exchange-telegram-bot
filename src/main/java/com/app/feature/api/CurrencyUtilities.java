package com.app.feature.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.Jsoup;


public final class CurrencyUtilities {
    public static final String RATES_JSON_PATH_NBU = "./src/main/resources/Currency_NBU_rates.json";
    public static final String RATES_JSON_PATH_PRIVATBANK = "./src/main/resources/Currency_Privat_rates.json";
    public static final String RATES_JSON_PATH_MONOBANK = "./src/main/resources/Currency_Mono_rates.json";

    /*
     * Monobank may return 429 HTTP status "Too many requests" if requested too often
     * 60_000 (60 seconds) timeout tested to be sufficient
     */
    public static final int UPDATE_PERIOD = 360_000;

    //Get request from API
    public static String getCurrencies(String url) throws IOException {
        return Jsoup
                .connect(url)
                .ignoreContentType(true)
                .get()
                .body()
                .text();
    }

    //check for API error
    public static boolean checkNbuCurrencyError() {
        return CurrencyJsonUpdateService.isNbuCheckErr();
    }

    public static boolean checkPrivatCurrencyError() {
        return CurrencyJsonUpdateService.isPrivatbankCheckErr();
    }

    public static boolean checkMonoCurrencyError() {
        return CurrencyJsonUpdateService.isMonobankCheckErr();
    }

    //write from json
    public static String readFromJsonFile(String fileName) {
        String result = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            result = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
