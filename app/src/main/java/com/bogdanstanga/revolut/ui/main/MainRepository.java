package com.bogdanstanga.revolut.ui.main;

import android.util.Pair;

import com.bogdanstanga.revolut.model.Currency;
import com.bogdanstanga.revolut.model.CurrencyDetails;
import com.bogdanstanga.revolut.model.RateExchangeResponse;
import com.bogdanstanga.revolut.network.ApiClient;
import com.bogdanstanga.revolut.util.CurrencyMapper;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;

public class MainRepository {

    public Observable<ArrayList<Currency>> getCurrencyList(String baseISO) {
        Pair<String, Double> baseCurrency = createBaseCurrency(baseISO);
        Observable<RateExchangeResponse> latestRatesObservable = ApiClient.rateExchangeService().getLatest(baseISO);
        Observable<HashMap<String, CurrencyDetails>> currencyDetailsObservable = ApiClient.currencyDetailsService().getDetails();

        return latestRatesObservable.zipWith(currencyDetailsObservable, (rateExchangeResponse, currencyDetails) -> {
            return CurrencyMapper.mapToList(rateExchangeResponse, currencyDetails, baseCurrency);
        });
    }

    public Observable<HashMap<String, Double>> getRatesUpdate(String baseISO) {
        Pair<String, Double> baseCurrency = createBaseCurrency(baseISO);
        return ApiClient.rateExchangeService().getLatest(baseISO).map(response -> CurrencyMapper.map(response, baseCurrency));
    }

    private Pair<String, Double> createBaseCurrency(String baseISO) {
        // The base currency always has the rate exchange 1
        return new Pair<>(baseISO, 1d);
    }

}
