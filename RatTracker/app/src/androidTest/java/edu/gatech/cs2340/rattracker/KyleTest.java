package edu.gatech.cs2340.rattracker;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import edu.gatech.cs2340.rattracker.controller.GraphActivity;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Kyle Suter on 11/13/2017.
 */

@RunWith(AndroidJUnit4.class)
public class KyleTest {

    @Rule
    public ActivityTestRule<GraphActivity> graphActivityRule
            = new ActivityTestRule<>(GraphActivity.class);


    //tests if the GraphActivity catches occurrences when the start date
    //is later than the end date
    @Test
    public void testBadGraphDateInputs() {
        onView(withId(R.id.graph_start_date))
                .perform(replaceText("10/10/2016"), closeSoftKeyboard());
        onView(withId(R.id.graph_end_date))
                .perform(replaceText("10/10/2015"), closeSoftKeyboard());
        onView(withId(R.id.graph_date_button)).perform(click());
        assertTrue(GraphActivity.getGraphData().isEmpty());
    }

    @Test
    public void testGoodGraphDateInputs() {
        onView(withId(R.id.graph_start_date))
                .perform(replaceText("10/10/2015"), closeSoftKeyboard());
        onView(withId(R.id.graph_end_date))
                .perform(replaceText("10/10/2016"), closeSoftKeyboard());
        onView(withId(R.id.graph_date_button)).perform(click());
        assertFalse(GraphActivity.getGraphData().isEmpty());
    }
}
