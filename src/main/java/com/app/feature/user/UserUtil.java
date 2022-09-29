package com.app.feature.user;

import com.app.feature.api.dto.Currency;
import com.app.feature.currency.dto.Bank;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class UserUtil {
    public static final String USERS_PATH = "./src/main/resources/users.json";
    public void write(List<UserInfo> usersList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(usersList);
        File file = new File(USERS_PATH);
        try (FileWriter fileWriter = new FileWriter(file))
        {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exception while writing to users.json file at \""
                    + USERS_PATH + "\"");
        }
    }

    public List<UserInfo> read() {
        File file = new File("./src/main/resources/users.json");

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        StringBuilder jsonBuilder = new StringBuilder();

        try (FileInputStream inputStream = new FileInputStream(file)) {
            int ch = inputStream.read();

            while (ch != -1) {
                jsonBuilder.append((char) ch);
                ch = inputStream.read();
            }
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }

        String json = jsonBuilder.toString();
        List<UserInfo> userList;
            if(json.isBlank()) {
            userList = new ArrayList<>();
        } else {
                Type typeToken = TypeToken
                        .getParameterized(List.class, UserInfo.class)
                        .getType();
                userList = new Gson().fromJson(json, typeToken);
        }
        return userList;
    }

    public UserInfo getUserById(List<UserInfo> usersList, Long userId) {
        return usersList.stream()
                .filter(it -> Objects.equals(it.getChatId(), userId))
                .findFirst().orElseThrow();
    }

    public List<Currency> getCurrencyTypeByUserId(Long userId) {
        List<UserInfo> usersList = read();
        return getUserById(usersList, userId).getUserCurrency();
    }

    public List<Bank> getBankTypeByUserId(Long userId) {
        List<UserInfo> usersList = read();
        return getUserById(usersList, userId).getUserBank();
    }

    public Integer getRoundingByUserId(Long userId) {
        List<UserInfo> usersList = read();
        return getUserById(usersList, userId).getRounding();
    }

    public String getAlarmTimeByUserId(Long userId) {
        List<UserInfo> usersList = read();
        return getUserById(usersList, userId).getAlarmTime();
    }

    public void setCurrencyTypeByUserId(Long userId, Currency currency) {
        List<UserInfo> usersList = read();
        UserInfo userById = getUserById(usersList, userId);
        List<Currency> userCurrency = userById.getUserCurrency();
        if(doesCurrencySaved(userCurrency, currency)) {
            userCurrency.remove(currency);
        } else {
            userCurrency.add(currency);
        }
        userById.setUserCurrency(userCurrency);

        write(usersList);
    }

    public void setBankTypeByUserId(Long userId, Bank bank) {
        List<UserInfo> usersList = read();
        UserInfo userById = getUserById(usersList, userId);
        List<Bank> userBanks = userById.getUserBank();
        if(doesBankSaved(userBanks, bank)) {
            userBanks.remove(bank);
        } else {
            userBanks.add(bank);
        }
        userById.setUserBank(userBanks);

        write(usersList);
    }

    public void setRoundingByUserId(Long userId, Integer number) {
        List<UserInfo> usersList = read();
        UserInfo userById = getUserById(usersList, userId);
        userById.setRounding(number);

        write(usersList);
    }

    public void setAlarmTimeByUserId(Long userId, String alarmTime) {
        List<UserInfo> usersList = read();
        UserInfo userById = getUserById(usersList, userId);
        userById.setAlarmTime(alarmTime);

        write(usersList);
    }

    private boolean doesCurrencySaved(List<Currency> saved, Currency current) {
        return saved.stream().anyMatch(currency -> currency.equals(current));
    }

    private boolean doesBankSaved(List<Bank> saved, Bank current) {
        return saved.stream().anyMatch(bank -> bank.equals(current));
    }
}
