package com.app.feature.api.dto;

import lombok.Data;

@Data
public abstract class AbstractCurrencyItem {
    protected String currency;
    protected Currency baseCurrency;
    protected float buy;
    protected float sale;
}