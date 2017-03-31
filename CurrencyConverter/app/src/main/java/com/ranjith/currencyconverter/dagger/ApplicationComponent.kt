package com.ranjith.currencyconverter.dagger

import com.ranjith.currencyconverter.CurrencyConverterApplication
import com.ranjith.currencyconverter.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created by ranjith on 2017-03-29.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, NetworkModule::class, FixerModule::class)) interface ApplicationComponent {
    fun inject(target : CurrencyConverterApplication)
    fun injectActivity(target: MainActivity)
}