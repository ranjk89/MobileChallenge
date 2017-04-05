package com.ranjith.currencyconverter.models

import java.util.*

/**
 * Created by ranjith on 2017-03-26.
 */

data class Conversion(val base: String?, val date: String?, val rates: Map<String, Double>?) {

    fun multiplyRatesBy(amount: Double) = rates?.map { Rate(it.key, it.value * amount) }
}

data class Rate(val currency: String, val exchangeRate: Double) {
    fun displayRate() = "%.2f".format(exchangeRate)
}
