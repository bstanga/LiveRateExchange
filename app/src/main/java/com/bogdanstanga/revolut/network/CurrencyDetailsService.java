package com.bogdanstanga.revolut.network;

import com.bogdanstanga.revolut.model.CurrencyDetails;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface CurrencyDetailsService {

    @GET("/Fluidbyte/2973986/raw/b0d1722b04b0a737aade2ce6e055263625a0b435/Common-Currency.json")
    Observable<HashMap<String, CurrencyDetails>> getDetails();

}
