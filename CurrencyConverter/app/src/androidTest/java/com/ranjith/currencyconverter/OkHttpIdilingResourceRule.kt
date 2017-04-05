package com.ranjith.currencyconverter

import android.support.test.espresso.Espresso
import com.jakewharton.espresso.OkHttp3IdlingResource
import okhttp3.OkHttpClient
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Created by ranjithkarunamurthy on 2017-04-02.
 */

class OkHttpIdlingResourceRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                val idlingResource = OkHttp3IdlingResource.create(
                        "okhttp", OkHttpClient.Builder().build())
                Espresso.registerIdlingResources(idlingResource)

                base.evaluate()

                Espresso.unregisterIdlingResources(idlingResource)
            }
        }
    }
}