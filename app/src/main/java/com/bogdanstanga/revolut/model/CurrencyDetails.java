package com.bogdanstanga.revolut.model;

public class CurrencyDetails {

    String name;
    String symbol;

    public CurrencyDetails(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}