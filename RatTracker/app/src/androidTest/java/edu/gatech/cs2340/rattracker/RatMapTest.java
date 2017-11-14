package edu.gatech.cs2340.rattracker;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.gatech.cs2340.rattracker.controller.RatMapActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

import static android.support.test.espresso.action.ViewActions.click;

/**
 * Created by kevinliao on 11/13/17.
 */

@RunWith(AndroidJUnit4.class)
public class RatMapTest {

    private EditText text;

    @Rule
    public ActivityTestRule<RatMapActivity> activity = new ActivityTestRule<>(RatMapActivity.class);

    @Test
    public void onDateSetTest() {

        // Button that calls class method
        onView(withId(R.id.start_date_text)).perform(replaceText("10/10/2016"), closeSoftKeyboard());
        onView(withId(R.id.end_date_text)).perform(replaceText("10/15/2017"), closeSoftKeyboard());
        onView(withId(R.id.select_date_button)).perform(click());
        text = activity.getActivity().getStartDateText();
        assertTrue(text.getText().toString().equals("10/10/2016"));

        text = activity.getActivity().getEndDateText();
        assertTrue(text.getText().toString().equals("10/15/2017"));
    }

}
