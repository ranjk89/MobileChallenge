package com.ranjith.currencyconverter

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.RecyclerView
import android.view.View
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.action.ViewActions.click
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.After


/**
 * Created by ranjithkarunamurthy on 2017-04-02.
 */

class MainActivityUITest {

    @get:Rule val mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, true, false)

    @get:Rule val okHttpIdlingResourceRule = OkHttpIdlingResourceRule()

    var mockWebServer : MockWebServer? = null

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()

        val mockResponse1 = "{\"base\": \"EUR\",\"date\": \"2017-03-31\",\"rates\": {\"AUD\": 1.3982, \"BGN\": 1.9558, \"BRL\": 3.38, \"CAD\": 1.4265 }}"
        mockWebServer?.enqueue(MockResponse().setBody(mockResponse1))
        mockWebServer?.enqueue(MockResponse().setBody("{\"base\": \"USD\",\"date\": \"2017-03-31\",\"rates\": {\"AUD\": 1.4982}}"))
        mockWebServer?.enqueue(MockResponse().setBody("{\"base\": \"USD\",\"date\": \"2017-03-31\",\"rates\": {\"CAD\": 1.2482}}"))
        mockWebServer?.enqueue(MockResponse().setBody("{\"base\": \"USD\",\"date\": \"2017-03-31\",\"rates\": {\"CAD\": 1.1482}}"))

        mockWebServer?.start()

    }

    @Test
    fun testCacheValidFor30Mins() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as TestCurrencyConverterApplication

        app.baseUrl = mockWebServer?.url("/").toString()
        mActivityRule.launchActivity(null)

        val editText = onView(withId(R.id.editText))
        editText.perform(typeText("1"))

        Thread.sleep(1000)

        val grid = onView(withId(R.id.grid))
        grid.check(matches(atPosition(0, hasDescendant(withText("1.40")))))

        val spinner = onView(withId(R.id.spinner))
        spinner.perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("USD"))).perform(click())

        Thread.sleep(1000)
        grid.check(matches(atPosition(0, hasDescendant(withText("1.50")))))

        editText.perform(typeText("2"))
        Thread.sleep(1000)
        grid.check(matches(atPosition(0, hasDescendant(withText("17.98")))))

        spinner.perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("CAD"))).perform(click())
        Thread.sleep(2000)
        grid.check(matches(atPosition(0, hasDescendant(withText("14.98")))))

        editText.perform(typeText("2"))
        Thread.sleep(500)
        grid.check(matches(atPosition(0, hasDescendant(withText("152.28")))))
    }

    @After
    fun tearDown() {
        mockWebServer?.shutdown()
    }

    fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
        checkNotNull(itemMatcher)
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position) ?: // has no item on such position
                        return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
}