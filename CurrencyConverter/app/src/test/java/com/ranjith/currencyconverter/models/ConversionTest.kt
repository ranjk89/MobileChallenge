package com.ranjith.currencyconverter.models

import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Test

/**
 * Created by ranjithkarunamurthy on 2017-04-02.
 */
class ConversionTest {
    @Test
    fun testMultiplyRatesBy() {
        val rates = mapOf(Pair("USD", 0.75), Pair("BLR", 21.44))
        var conversion = Conversion("CAD", "2017-03-31", rates)
        val  ratesMultipliedBy1 = conversion.multiplyRatesBy(1.0)

        assertThat(ratesMultipliedBy1?.get(0)?.exchangeRate, CoreMatchers.`is`(rates["USD"]))

        var ratesTimes = conversion.multiplyRatesBy(1.5)
        assertThat(ratesTimes?.get(0)?.exchangeRate, CoreMatchers.`is`(0.75 * 1.5))

        conversion = Conversion("CAD", "2017-02-25", null)
        assertThat(conversion.rates, CoreMatchers.`is`(CoreMatchers.nullValue()))

        conversion = Conversion("CAD", "2017-02-25", emptyMap())
        assertThat(conversion.rates, CoreMatchers.`is`(emptyMap()))
    }

    @Test
    fun testRateFormatting() {
        var rate = Rate("CAD", 124.5677)
        assertThat(rate.displayRate(), CoreMatchers.`is`("124.57"))

        rate = Rate("CAD", 124.5639)
        assertThat(rate.displayRate(), CoreMatchers.`is`("124.56"))

        rate = Rate("EUR", 123.0)
        assertThat(rate.displayRate(), CoreMatchers.`is`("123.00"))

        rate = Rate("DHR", 0.0)
        assertThat(rate.displayRate(), CoreMatchers.`is`("0.00"))
    }
}