package com.ranjith.currencyconverter

import android.view.View
import android.widget.Spinner
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import rx.observers.TestSubscriber

/**
 * Created by ranjith on 2017-03-31.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class MainActivityTest {

    var mainActivity : MainActivity? = null

    @Before
    fun setUp() {
        mainActivity = Robolectric.setupActivity(MainActivity::class.java)
    }

    @Test
    fun viewsAreNotNull() {
        assertThat(mainActivity, `is`(not(nullValue())))
        assertThat(mainActivity?.findViewById(R.id.editText), `is`(not(nullValue())))
        assertThat(mainActivity?.findViewById(R.id.spinner), `is`(not(nullValue())))
        assertThat(mainActivity?.findViewById(R.id.grid), `is`(not(nullValue())))
    }

    @Test
    fun viewsAreVisible() {
        assertThat(mainActivity, `is`(not(nullValue())))
        assertThat(mainActivity?.findViewById(R.id.editText)?.visibility, `is`(View.VISIBLE))
        assertThat(mainActivity?.findViewById(R.id.spinner)?.visibility, `is`(View.VISIBLE))
        assertThat(mainActivity?.findViewById(R.id.grid)?.visibility, `is`(View.VISIBLE))
    }

    @Test
    fun subscriptionsAreClearedOnDestroy() {
        val mainActivity = Robolectric.buildActivity(MainActivity::class.java).create().visible().destroy().get()
        assertThat(mainActivity?.subscription?.isUnsubscribed, `is`(true))
    }

    @Test
    fun selectedCurrencyIsPopulated() {
        var currencySelector = mainActivity?.findViewById(R.id.spinner) as Spinner

        val testSubscriber = TestSubscriber<String>()
        mainActivity?.currencySelectedObservable(currencySelector, mainActivity?.currencies)?.subscribe(testSubscriber)

        testSubscriber.assertValue("AUD")
        testSubscriber.assertNoTerminalEvent()

        currencySelector.setSelection(2)
        testSubscriber.assertValuesAndClear("AUD","BRL")

        currencySelector.setSelection(2)
        testSubscriber.assertNoValues()

        currencySelector.setSelection(-1)
        testSubscriber.assertNoValues()
        testSubscriber.assertNoErrors()

    }
}