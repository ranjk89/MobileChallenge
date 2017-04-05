package com.ranjith.currencyconverter

import com.ranjith.currencyconverter.dagger.CurrencyComponent

/**
 * Created by ranjithkarunamurthy on 2017-04-02.
 */
class TestCurrencyConverterApplication : CurrencyConverterApplication() {

    var baseUrl : String? = null

    override fun provideBaseUrl() : String {
      return baseUrl?:""
    }
}