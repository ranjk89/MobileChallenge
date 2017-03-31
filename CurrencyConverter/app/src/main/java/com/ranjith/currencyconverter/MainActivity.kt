package com.ranjith.currencyconverter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.widget.*
import com.jakewharton.rxbinding.widget.RxAdapterView
import com.jakewharton.rxbinding.widget.RxTextView
import com.ranjith.currencyconverter.models.Conversion
import com.ranjith.currencyconverter.retrofit.FixerApi
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
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

        (application as CurrencyConverterApplication).appComponent?.injectActivity(this)

        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1, currencies)

        subscription.add(Observable.combineLatest(
                amountChangedObservable(editText),
                currencySelectedObservable(spinner, currencies),
                { t1, t2 -> Pair(t1, t2) }
        ).subscribe({
            val amount = it.first.toString().toDouble()
            currentConversionObservable(it.second, fixerApi)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ grid.adapter = CurrencyAdapter(amount, it.rates) },
                            { Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show() })
        }))

        grid.layoutManager = GridLayoutManager(this, 3)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription.unsubscribe()
    }

    fun currencySelectedObservable(spinner: Spinner, currencies: Array<String>?): Observable<String> = RxAdapterView.itemSelections(spinner).distinctUntilChanged().filter { it >= 0 }.map { currencies?.get(it) }

    fun amountChangedObservable(editText: EditText): Observable<CharSequence> = RxTextView.textChanges(editText).filter { !it.isNullOrEmpty() }.debounce(1, TimeUnit.SECONDS)

    fun currentConversionObservable(currency: String, fixerApi: FixerApi): Observable<Conversion> = fixerApi.getCurrentValues(currency)
}
