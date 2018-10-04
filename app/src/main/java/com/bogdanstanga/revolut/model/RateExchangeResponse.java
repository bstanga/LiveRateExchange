package com.bogdanstanga.revolut.model;

import java.util.HashMap;

public class RateExchangeResponse {

    String base;
    String date;
    HashMap<String, Double> rates;

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public HashMap<String, Double> getCurrencyRates() {
        return rates;
    }
}
