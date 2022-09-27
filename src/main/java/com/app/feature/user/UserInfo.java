package com.app.feature.user;

import com.app.feature.currency.dto.Bank;
import com.app.feature.currency.dto.Currency;
import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
    private Long chatId;
    private String firstName;
    private String userName;
    private List<Currency> userCurrency;
    private List<Bank> userBank;
    private Integer rounding;
    private String alarmTime;

    public UserInfo(Long chatId, String firstName, String userName, Currency userCurrency, Bank userBank, int rounding, String alarmTime) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.userName = userName;
        this.userCurrency = List.of(userCurrency);
        this.userBank = List.of(userBank);
        this.rounding = rounding;
        this.alarmTime = alarmTime;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", userName='" + userName + '\'' +
                ", userCurrency=" + userCurrency +
                ", userBank=" + userBank +
                ", rounding=" + rounding +
                ", alarmTime=" + alarmTime +
                '}';
    }
}
