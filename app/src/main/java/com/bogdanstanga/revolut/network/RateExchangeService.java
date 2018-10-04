package com.bogdanstanga.revolut.network;

import com.bogdanstanga.revolut.model.RateExchangeResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RateExchangeService {

    @GET("/latest")
    Observable<RateExchangeResponse> getLatest(@Query("base") String base);

}
