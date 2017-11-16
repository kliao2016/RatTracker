package edu.gatech.cs2340.rattracker.test;

import android.os.IBinder;
import android.support.test.espresso.Root;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.view.WindowManager;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import edu.gatech.cs2340.rattracker.controller.AddReport;
import edu.gatech.cs2340.rattracker.R;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ValidateInputTest {
    @Rule
    public ActivityTestRule<AddReport> arRule = new ActivityTestRule<>(
            AddReport.class);

    @Test
    public void EmptyInputs() {
        // because we use date/time select and autocomplete for address,
        // empty inputs are all that need to be checked
        onView(withId(R.id.date)).perform(typeText(""));
        onView(withId(R.id.addReportButton)).perform(click());
        onView(withText(R.string.invalid_date)).inRoot(new ToastMatcher())
                .check(matches(withText("Invalid input: you must enter a date")));

        onView(withId(R.id.date)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.addReportButton)).perform(click());
        onView(withText(R.string.invalid_time)).inRoot(new ToastMatcher())
                .check(matches(withText("Invalid input: you must enter a time")));

        onView(withId(R.id.time)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.addReportButton)).perform(click());
        onView(withText(R.string.invalid_address)).inRoot(new ToastMatcher())
                .check(matches(withText("Invalid input: you must enter a address")));
    }

    @Test
    public void ValidInputs() {
        onView(withId(R.id.date)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.time)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.address)).perform(typeText("100 10 St Nw, Atlanta, GA 30309"));

        onView(withId(R.id.addReportButton)).perform(click());
    }



    private class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    return true;
                }
            }
            return false;
        }
    }
}
