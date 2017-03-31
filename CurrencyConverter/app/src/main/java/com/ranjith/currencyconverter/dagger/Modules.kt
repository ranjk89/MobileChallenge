package com.ranjith.currencyconverter.dagger

import android.content.Context
import com.ranjith.currencyconverter.CurrencyConverterApplication
import com.ranjith.currencyconverter.retrofit.FixerApi
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named

/**
 * Created by ranjith on 2017-03-29.
 */
@Module class AppModule(val app: CurrencyConverterApplication) {
    @Provides fun provideContext(): Context = app
}

@Module class NetworkModule {

    @Provides fun provideCache(context: Context) : Cache? {
        var cache: Cache? = null
        try {
            cache = Cache(File(context.cacheDir, "http-cache"),
                    1 * 1024 * 1024)
        } catch (e: Exception) {
        }

        return cache
    }

    @Provides fun provideCacheExpiryMins() = 30

    @Provides fun provideCacheControl(expiryMins: Int) = CacheControl.Builder()
            .maxAge(expiryMins, TimeUnit.MINUTES)
            .build()

    @Provides fun provideOkHttpClient(cache: Cache?, cacheControl: CacheControl): OkHttpClient {
        return OkHttpClient.Builder()
                .addNetworkInterceptor {
                    val response = it.proceed(it.request())

                    response.newBuilder()
                            .header("Cache-Control", cacheControl.toString())
                            .build()
                }
                .cache(cache)
                .build()
    }

    @Provides fun provideRetrofit( baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build()
    }

}

@Module class FixerModule {
    @Provides fun provideBaseUrl() = "http://api.fixer.io/"

    @Provides fun provideFixerApi(retrofit : Retrofit) = retrofit.create(FixerApi::class.java)

    @Provides fun provideCurrencies() = arrayOf(
            "AUD",
            "BGN",
            "BRL",
            "CAD",
            "CHF",
            "CNY",
            "CZK",
            "DKK",
            "EUR",
            "GBP",
            "HKD",
            "HRK",
            "HUF",
            "IDR",
            "ILS",
            "INR",
            "JPY",
            "KRW",
            "MXN",
            "MYR",
            "NOK",
            "NZD",
            "PHP",
            "PLN",
            "RON",
            "RUB",
            "SEK",
            "SGD",
            "THB",
            "TRY",
            "USD",
            "ZAR")
}