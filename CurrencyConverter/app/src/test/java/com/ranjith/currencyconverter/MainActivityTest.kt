package com.ranjith.currencyconverter

import android.view.View
import android.widget.EditText
import android.widget.Spinner
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import rx.observers.TestSubscriber
import rx.schedulers.TestScheduler
import java.util.concurrent.TimeUnit

/**
 * Created by ranjith on 2017-03-31.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class MainActivityTest {

    var mainActivity : MainActivity? = null
    var activityController : ActivityController<MainActivity>? = null

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(MainActivity::class.java).create().visible()
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
        mainActivity = activityController?.destroy()?.get()
        assertThat(mainActivity?.subscription?.isUnsubscribed, `is`(true))
    }

    @Test
    fun selectedCurrencyIsPopulated() {
        val currencySelector = mainActivity?.findViewById(R.id.spinner) as Spinner

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

    @Test
    fun amountChangeEventEverySecond() {
        val amountInput = mainActivity?.findViewById(R.id.editText) as EditText

        val testScheduler = TestScheduler()
        val testSubscriber = TestSubscriber<CharSequence>()
        mainActivity?.amountChangedObservable(amountInput, testScheduler)?.subscribe(testSubscriber)

        testSubscriber.assertNoErrors()
        testSubscriber.assertNoTerminalEvent()

        val amount1 = "12.34"
        amountInput.setText("12")
        amountInput.setText(amount1)
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        amountInput.setText(amount1.plus("2"))
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(amount1)

        amountInput.setText("123.45")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        amountInput.setText("123.456")
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        testSubscriber.assertValues(amount1, "123.45", "123.456")
    }

    @Test
    fun testAmountChangedTriggersOnlyForNewInput() {
        val amountInput = mainActivity?.findViewById(R.id.editText) as EditText
        val currencySelector = mainActivity?.findViewById(R.id.spinner) as Spinner

        var testSubscriber = TestSubscriber<Pair<String, String>>()
        var testScheduler = TestScheduler()
        mainActivity?.currencyValueChangedObservable(testScheduler)?.subscribe(testSubscriber)

        amountInput.setText("100.50")
        currencySelector.setSelection(2)
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        testSubscriber.assertValuesAndClear(Pair("100.50", "BRL"))

        amountInput.setText("100.50")
        currencySelector.setSelection(2)
        testSubscriber.assertNoValues()

        currencySelector.setSelection(4)
        testSubscriber.assertValuesAndClear(Pair("100.50", "CHF"))

        amountInput.setText("250.00")
        testSubscriber.assertNoValues()
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        testSubscriber.assertValuesAndClear(Pair("250.00", "CHF"))
    }
}