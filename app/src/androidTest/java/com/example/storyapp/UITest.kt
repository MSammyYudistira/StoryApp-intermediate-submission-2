package com.example.storyapp

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.storyapp.ui.auth.LoginActivity
import com.example.storyapp.ui.homepage.HomePageActivity
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var activityScenario: ActivityScenario<LoginActivity>

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
        activityScenario = activityRule.scenario
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun loginAndLogout() {
        // Login Test
        onView(withId(R.id.ed_login_email)).perform(typeText("sammy123@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password)).perform(typeText("asd111111"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // Verify loading indicator is shown
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))

        // Wait for API response with idling resource
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        // redirect to HomePageActivity
        ActivityScenario.launch(HomePageActivity::class.java)

        // Verify user is logged in with checking displayed data
        onView(withId(R.id.rv_stories))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rv_stories)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                10
            )
        )

        // Logout Test
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.logout)).perform(click())
        onView(withText(R.string.logout_confirmation)).check(matches(isDisplayed()))
        onView(withText(R.string.yes)).perform(click())

        // Verify user is logged out
        onView(withId(R.id.titleTextView)).check(matches(withText(R.string.login)))
    }
}
