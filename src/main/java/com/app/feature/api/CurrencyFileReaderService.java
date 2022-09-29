package com.app.feature.api;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
public final class CurrencyFileReaderService {
    public String getRatesForNbu() {
        return getRatesFromJsonFile(CurrencyUpdaterFileWriterService.RATES_JSON_PATH_NBU);
    }

    public String getRatesForPrivatbank() {
        return getRatesFromJsonFile(CurrencyUpdaterFileWriterService.RATES_JSON_PATH_PRIVATBANK);
    }

    public String getRatesForMonobank() {
        return getRatesFromJsonFile(CurrencyUpdaterFileWriterService.RATES_JSON_PATH_MONOBANK);
    }

    private String getRatesFromJsonFile(String fileName) {
        String json = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            json = reader.readLine();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            log.info("File reading error while reading " + fileName);
        }
        return json;
    }
}
