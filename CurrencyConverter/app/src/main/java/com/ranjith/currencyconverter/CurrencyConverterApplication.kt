package com.ranjith.currencyconverter

import android.app.Application
import com.ranjith.currencyconverter.dagger.*
import dagger.Provides
import javax.inject.Inject

/**
 * Created by ranjith on 2017-03-29.
 */
open class CurrencyConverterApplication : Application() {

    var appComponent : CurrencyComponent? = null

    open fun provideBaseUrl() = "http://api.fixer.io/"

    open fun createComponent(): CurrencyComponent {
        var context = applicationContext as CurrencyConverterApplication
        return DaggerApplicationComponent.builder()
                .appModule(AppModule(context))
                .networkModule(NetworkModule(context.provideBaseUrl()))
                .build()
    }
}