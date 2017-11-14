package edu.gatech.cs2340.rattracker;

import android.support.test.rule.ActivityTestRule;
import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.gatech.cs2340.rattracker.controller.GraphActivity;
import edu.gatech.cs2340.rattracker.controller.RatMapActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

import static android.support.test.espresso.action.ViewActions.click;

/**
 * Created by kevinliao on 11/13/17.
 */

public class RatMapTest {

    @Rule
    public ActivityTestRule<RatMapActivity> mapActivity = new ActivityTestRule<>(RatMapActivity.class);

    @Test
    public void loadPinsWithoutRangeMapNullTest() {
        // Assert not null before method call
        assertTrue(mapActivity.getActivity().getReportMap() != null);

        // Button that calls class method
        onView(withId(R.id.select_date_button)).perform(click());

        // Assert not null after method call
        assertTrue(mapActivity.getActivity().getReportMap() != null);
    }

}
