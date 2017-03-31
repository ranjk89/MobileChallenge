package com.ranjith.currencyconverter.models

import java.util.*

/**
 * Created by ranjith on 2017-03-26.
 */

data class Conversion(val base: String?, val date: String?, val rates: HashMap<String, Double>?)
