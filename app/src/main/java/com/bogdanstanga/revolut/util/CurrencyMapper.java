package com.bogdanstanga.revolut.util;

import android.util.Pair;

import com.bogdanstanga.revolut.model.Currency;
import com.bogdanstanga.revolut.model.CurrencyDetails;
import com.bogdanstanga.revolut.model.RateExchangeResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CurrencyMapper {

    public static ArrayList<Currency> mapToList(RateExchangeResponse rateExchangeResponse, HashMap<String, CurrencyDetails> currencyDetails, Pair<String, Double> baseCurrency) {
        ArrayList<Currency> list = new ArrayList<>();
        list.add(buildCurrency(baseCurrency.first, baseCurrency.second, currencyDetails));
        for (Map.Entry<String, Double> entry : rateExchangeResponse.getCurrencyRates().entrySet()) {
            list.add(buildCurrency(entry.getKey(), entry.getValue(), currencyDetails));
        }

        return list;
    }

    public static HashMap<String, Double> map(RateExchangeResponse response, Pair<String, Double> baseCurrency) {
        HashMap<String, Double> rates = response.getCurrencyRates();
        rates.put(baseCurrency.first, baseCurrency.second);
        return rates;
    }

    private static Currency buildCurrency(String iso, double exchangeRate, HashMap<String, CurrencyDetails> currencyDetails) {
        CurrencyDetails details = currencyDetails.get(iso);

        String title = details.getName();
        if (details.getSymbol() != null && !details.getSymbol().equals(iso)) {
            title += " (" + details.getSymbol() + ")";
        }

        return new Currency(title, iso, exchangeRate);
    }

}
