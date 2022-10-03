package com.app.feature.api;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CurrencyUpdaterFileWriterService implements Runnable {
    public static final String RATES_JSON_PATH_NBU = "./src/main/resources/Currency_NBU_rates.json";
    public static final String RATES_JSON_PATH_PRIVATBANK = "./src/main/resources/Currency_Privat_rates.json";
    public static final String RATES_JSON_PATH_MONOBANK = "./src/main/resources/Currency_Mono_rates.json";

    /*
     * Monobank may return 429 HTTP status "Too many requests" if requested too often
     * 60_000 (60 seconds) timeout tested to be sufficient
     * According to information on site, information is chashed and posted no
     * more often than every 5 minutes
     */
    public static final int UPDATE_PERIOD_SECONDS = 360;

    @Getter
    private static boolean nbuCheckErr = true;
    @Getter
    private static boolean privatbankCheckErr = true;
    @Getter
    private static boolean monobankCheckErr = true;

    @Override
    public void run() {

        //NBU currency update
        final Runnable nbu = () ->
        {
            while (true) {
                try {
                    String json = new CurrencyUpdaterBankService().getRatesForNbu();

                    File file = new File(RATES_JSON_PATH_NBU);
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(json);
                        nbuCheckErr = false;
                        log.info("NBU rates updated");
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.err.println("Can't write NBU rate update to file");
                        nbuCheckErr = true;
                    }
                } catch (IllegalStateException | IOException e) {
                    System.err.println(e.getMessage());
                    log.info("NBU API request error");
                }
                waitUpdateTimeout();
            }
        };

        //Privatbank currency update
        final Runnable privatbank = () ->
        {
            while (true) {
                try {
                    String json = new CurrencyUpdaterBankService().getRatesForPrivatbank();

                    File file = new File(RATES_JSON_PATH_PRIVATBANK);
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(json);
                        privatbankCheckErr = false;
                        log.info("Privatbank rates updated");
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.err.println("Can't write Privatbank rate update to file");
                        privatbankCheckErr = true;
                    }
                } catch (IllegalStateException | IOException e) {
                    System.err.println(e.getMessage());
                    log.info("Privatbank API request error");
                }
                waitUpdateTimeout();
            }
        };

        //Monobank currency update
        final Runnable monobank = () ->
        {
            while (true) {
                try {
                    String json = new CurrencyUpdaterBankService().getRatesForMonobank();

                    File file = new File(RATES_JSON_PATH_MONOBANK);
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(json);
                        monobankCheckErr = false;
                        log.info("Monobank rates updated");
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.err.println("Can't write Monobank rate update to file");
                        monobankCheckErr = true;
                    }
                } catch (IllegalStateException | IOException e) {
                    System.err.println(e.getMessage());
                    log.info("Monobank API request error");
                }
                waitUpdateTimeout();
            }
        };

        new Thread(nbu).start();
        log.info("NBU API downloader thread has been started");
        new Thread(privatbank).start();
        log.info("Privatbank API downloader thread has been started");
        new Thread(monobank).start();
        log.info("Monobank API downloader thread has been started");
    }

    private void waitUpdateTimeout() {
        try {
            TimeUnit.SECONDS.sleep(UPDATE_PERIOD_SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}