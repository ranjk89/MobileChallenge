package com.ranjith.currencyconverter.retrofit;

import com.ranjith.currencyconverter.models.Conversion;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by ranjith on 2017-03-26.
 */

public interface FixerApi {
    @GET("latest")
    Observable<Conversion> getCurrentValues(@Query(value = "base", encoded = true) String baseCurrency);
}
