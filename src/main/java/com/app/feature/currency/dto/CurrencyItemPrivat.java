package com.app.feature.currency.dto;

import lombok.Data;

@Data
public class CurrencyItemPrivat {
    private Currency ccy;
    private Currency base_ccy;
    private float buy;
    private float sale;
}
