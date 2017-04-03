package com.ranjith.currencyconverter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.jakewharton.rxbinding.widget.RxAdapterView
import com.jakewharton.rxbinding.widget.RxTextView
import com.ranjith.currencyconverter.dagger.AppModule
import com.ranjith.currencyconverter.dagger.CurrencyComponent
import com.ranjith.currencyconverter.dagger.DaggerApplicationComponent
import com.ranjith.currencyconverter.dagger.NetworkModule
import com.ranjith.currencyconverter.models.Conversion
import com.ranjith.currencyconverter.retrofit.FixerApi
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import rx.Observable
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject lateinit var retrofit : Retrofit
    @Inject lateinit var fixerApi : FixerApi
    @Inject lateinit var currencies : Array<String>

    var subscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inject dagger dependencies
        val currencyConverterApplication = application as CurrencyConverterApplication
        currencyConverterApplication.appComponent = currencyConverterApplication.createComponent()

        currencyConverterApplication.appComponent?.injectActivity(this)

        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1, currencies) as SpinnerAdapter?

        subscription.add(currencyValueChangedObservable().subscribe({
            showSpinner()
            val amount = it.first.toDouble()
            subscription.add(currentConversionObservable(it.second, fixerApi)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({
                        updateConversions(amount, it)
                        hideSpinner()
                    },
                    {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        hideSpinner()
                    }))
        }))

        grid.layoutManager = GridLayoutManager(this, 3) as RecyclerView.LayoutManager?
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.unsubscribe()
    }

    private fun showSpinner() {
        progressSpinner.visibility = View.VISIBLE
    }

    private fun hideSpinner() {
        progressSpinner.visibility = View.GONE
    }

    private fun updateConversions(amount: Double, conversion: Conversion) {
        if (grid.adapter == null) {
            grid.adapter = CurrencyAdapter(conversion.multiplyRatesBy(amount))
        } else {
            val currencyAdapter = grid.adapter as CurrencyAdapter
            currencyAdapter.rates = conversion.multiplyRatesBy(amount)
            currencyAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Combines the latest values from amount edit text and currency spinner
     *
     * @return Observable that combines the latest data from amount input field and currency spinner
     * @param scheduler Scheduler to run debounce operation on (this helps with testing)
     */
    fun currencyValueChangedObservable(scheduler: Scheduler = AndroidSchedulers.mainThread()): Observable<Pair<String, String>> {
        return Observable.combineLatest(
                amountChangedObservable(editText, scheduler),
                currencySelectedObservable(spinner, currencies),
                { t1, t2 -> Pair(t1, t2) }
        ).filter { !it.first.isNullOrEmpty() && !it.second.isNullOrEmpty() }
    }

    /**
     * Creates an observable from spinner selection and transforms it in to currency code
     *
     * @return Observable from spinner selection
     * @param spinner Spinner for selection events
     * @param currencies List of currency strings
     */
    fun currencySelectedObservable(spinner: Spinner, currencies: Array<String>?): Observable<String> = RxAdapterView.itemSelections(spinner).distinctUntilChanged().filter { it >= 0 }.map { currencies?.get(it) }

    /**
     * Creates an observable from text changes on editText on scheduler
     *
     * @return Observable from text change events
     * @param editText EditText for text change events
     * @param scheduler Scheduler to run debounce operation on (this helps with testing)
     */
    fun amountChangedObservable(editText: EditText, scheduler: Scheduler = AndroidSchedulers.mainThread()): Observable<String> = RxTextView.textChanges(editText).filter { !it.isNullOrEmpty() }.debounce(300, TimeUnit.MILLISECONDS, scheduler).map { it.toString() }

    /**
     * Creates an observable from fixerApi retrofit client and gives a Conversion object
     *
     * @return Observable to talk to Fixer API
     * @param currency Currency code string
     * @param fixerApi Retrofit fixer api
     */
    fun currentConversionObservable(currency: String, fixerApi: FixerApi?): Observable<Conversion>? = fixerApi?.getCurrentValues(currency)
}
