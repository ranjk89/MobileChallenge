package com.ranjith.currencyconverter

import android.app.Application
import com.ranjith.currencyconverter.dagger.AppModule
import com.ranjith.currencyconverter.dagger.ApplicationComponent
import com.ranjith.currencyconverter.dagger.DaggerApplicationComponent

/**
 * Created by ranjith on 2017-03-29.
 */
class CurrencyConverterApplication : Application() {

    var appComponent : ApplicationComponent? = null

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerApplicationComponent.builder()
                .appModule(AppModule(this))
                .build()

        appComponent?.inject(this)
    }
}