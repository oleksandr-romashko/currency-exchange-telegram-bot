package com.app.feature.user;

import com.app.feature.currency.dto.Currency;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserUtil {
    public void write(List<UserInfo> usersList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(usersList);
        File users = new File("./src/main/resources/users.json");
        if (!users.exists()) {
            users.getParentFile().mkdirs();
            try {
                users.createNewFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream(users)) {
            outputStream.write(json.getBytes());
        } catch (IOException e) {
            System.err.println("Exception!!!" + e.getMessage());
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
        String usersJson = "";

        try (FileInputStream inputStream = new FileInputStream(file)) {
            int ch = inputStream.read();

            while (ch != -1) {
                usersJson += (char) ch;
                ch = inputStream.read();
            }
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }

        List<UserInfo> users;
        if(usersJson.equals("")) {
            users  = new ArrayList<>();
        } else {
            users = new Gson().fromJson(usersJson, new TypeToken<List<UserInfo>>() {}.getType());
        }
        return users;
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

    public void setCurrencyTypeByUserId(Long userId, Currency currency) {
        List<UserInfo> usersList = read();
        UserInfo userById = getUserById(usersList, userId);
        List<Currency> userCurrency = userById.getUserCurrency();
        if(!doesCurrencySaved(userCurrency, currency)) {
            userCurrency.add(currency);
        } else {
            userCurrency.remove(currency);
        }
        userById.setUserCurrency(userCurrency);

        write(usersList);
    }

    private boolean doesCurrencySaved(List<Currency> saved, Currency current) {
        return !saved.stream().filter(currency -> currency.equals(current)).findFirst().isEmpty();
    }
}
