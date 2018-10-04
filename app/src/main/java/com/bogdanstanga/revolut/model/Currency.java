package com.bogdanstanga.revolut.model;

public class Currency {

    private String name;
    private String ISO;
    private double amount;

    public Currency(String name, String ISO, double amount) {
        this.name = name;
        this.ISO = ISO;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getISO() {
        return ISO;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCountryFlagUrl() {
        String shortISO = ISO.substring(0, 2).toLowerCase();
        return "https://raw.githubusercontent.com/hjnilsson/country-flags/master/png100px/" + shortISO + ".png";
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + ISO.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Currency && ((Currency) obj).getISO().equals(getISO());
    }

    @Override
    public String toString() {
        return "Currency{" +
                "ISO='" + ISO + '\'' +
                ", amount=" + amount +
                '}';
    }
}
