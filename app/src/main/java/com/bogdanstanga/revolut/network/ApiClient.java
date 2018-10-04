package com.bogdanstanga.revolut.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String RATE_EXCHANGE_BASE_URL = "https://revolut.duckdns.org/";
    private static final String CURRENCY_DETAILS_BASE_URL = "https://gist.githubusercontent.com/";

    private static Retrofit rateExchangeRetrofit;
    private static Retrofit currencyDetailsRetrofit;

    private static Retrofit rateExchangeClient() {
        if (rateExchangeRetrofit == null) {
            rateExchangeRetrofit = buildRetrofit(RATE_EXCHANGE_BASE_URL);
        }
        return rateExchangeRetrofit;
    }

    private static Retrofit currencyDetailsClient() {
        if (currencyDetailsRetrofit == null) {
            currencyDetailsRetrofit = buildRetrofit(CURRENCY_DETAILS_BASE_URL);
        }
        return currencyDetailsRetrofit;
    }

    private static Retrofit buildRetrofit(String baseUrl) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static RateExchangeService rateExchangeService() {
        return rateExchangeClient().create(RateExchangeService.class);
    }

    public static CurrencyDetailsService currencyDetailsService() {
        return currencyDetailsClient().create(CurrencyDetailsService.class);
    }

}
