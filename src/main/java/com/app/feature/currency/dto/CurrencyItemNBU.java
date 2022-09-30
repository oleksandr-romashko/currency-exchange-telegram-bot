package com.app.feature.currency.dto;

import lombok.Data;

@Data
public class CurrencyItemNBU {
    private int r030;
    private String txt;
    private float rate;
    private Currency cc;
    private String exchangeDate;
}
