package com.ranjith.currencyconverter.dagger

import com.ranjith.currencyconverter.CurrencyConverterApplication
import com.ranjith.currencyconverter.MainActivity

/**
 * Created by ranjithkarunamurthy on 2017-04-03.
 */
interface CurrencyComponent {
    fun inject(target: Any)
    fun injectActivity(target: MainActivity)
    fun inject(target : CurrencyConverterApplication)
}