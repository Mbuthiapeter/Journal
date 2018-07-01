package com.friki.mbuthia.journalapp.authPack;

import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.friki.mbuthia.journalapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mRegisterTestRule =
            new ActivityTestRule<RegisterActivity>(RegisterActivity.class);

    @Before
    public void setUp() throws Exception {
        mRegisterTestRule.getActivity();
    }


    //Tests the error message textview if its Invisible on activity start
    @Test
    public void testThatErrorMessageIsInitiallyInvisible() throws Exception{
        onView(withId(R.id.tvErrorMsg)).check(matches(not(isDisplayed())));

    }

    //Tests the password rule
    @Test
    public void testPasswordRule(){
        onView(withId(R.id.etPassword)).perform(typeText("abc"));
        onView(withId(R.id.button)).perform(click());

        onView(withId(R.id.tvErrorMsg))
                .check(matches(isDisplayed()))
                .check(matches(withText("Password is too short \n\n\n")));
    }

    @Test
    public void testValidPassword() {
        onView(withId(R.id.etPassword)).perform(typeText("Abc123"));
        onView(withId(R.id.etEmail)).perform(typeText("peter@gmail.com"));
        onView(withId(R.id.button)).perform(click());

        onView(withId(R.id.tvErrorMsg)).check(matches(isDisplayed()));
    }

    //Testing the toast message displayed on account duplication
    @Test
    public void testTheToastMessageDisplayed(){
        onView(withId(R.id.etPassword)).perform(typeText("Abc123"));
        onView(withId(R.id.etEmail)).perform(typeText("peter@gmail.com"));
        onView(withId(R.id.button)).perform(click());

        onView(withText(R.string.account_failuer)).inRoot(withDecorView(not(is(mRegisterTestRule.getActivity().getWindow()
                .getDecorView())))).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
    }
}