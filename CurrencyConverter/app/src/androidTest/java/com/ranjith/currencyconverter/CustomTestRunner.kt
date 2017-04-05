package com.ranjith.currencyconverter

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner



/**
 * Created by ranjithkarunamurthy on 2017-04-02.
 */
class CustomTestRunner : AndroidJUnitRunner() {
    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(
            cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestCurrencyConverterApplication::class.java.name, context)
    }
}