package com.app.feature.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.Jsoup;
import java.util.concurrent.TimeUnit;


public final class Utilities {

    //Get request from API
    public static String getAPIRequest(String url) {
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
            throw new IllegalStateException("getAPIRequest method error");
        }
        return json;
    }

    //check for API error
    public static boolean checkNbuCurrencyError() {
        return CurrencyJsonUpdate.isNbuCheckErr();
    }

    public static boolean checkPrivatCurrencyError() {
        return CurrencyJsonUpdate.isPrivatCheckErr();
    }

    public static boolean checkMonoCurrencyError() {
        return CurrencyJsonUpdate.isMonoCheckErr();
    }

    //wait
    public static void wait(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //write from json
    public static String writeFromJsonFile(String fileName) {
        String result = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            result = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
