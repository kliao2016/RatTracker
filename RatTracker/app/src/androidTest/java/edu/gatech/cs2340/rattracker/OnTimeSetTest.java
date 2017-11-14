package edu.gatech.cs2340.rattracker;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import org.junit.Test;

import edu.gatech.cs2340.rattracker.controller.AddReport;

/**
 * Created by bglow on 11/13/2017.
 */

public class OnTimeSetTest extends ActivityInstrumentationTestCase2<AddReport> {
    private AddReport mAddReport;
    private EditText timeText;

    public OnTimeSetTest() {
        super(AddReport.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mAddReport = getActivity();
        timeText = mAddReport.findViewById(R.id.time);
    }

    @Test
    public void testConvertTime() {
        String result;

        //Test 1: Minute >= 10
        result = mAddReport.convertTime(8, 12);
        assertEquals("8:12:00 AM", result);

        //Test 2: Minute < 10
        result = mAddReport.convertTime(9, 8);
        assertEquals("9:08:00 AM", result);

        //Test 3: Hour < 12, Hour == 0
        result = mAddReport.convertTime(0, 11);
        assertEquals("12:11:00 AM", result);

        //Test 4: Hour < 12, Hour != 0
        result = mAddReport.convertTime(5, 25);
        assertEquals("5:25:00 AM", result);

        //Test 5: Hour >= 12, Hour != 12
        result = mAddReport.convertTime(15, 15);
        assertEquals("3:15:00 PM", result);

        //Test 6: Hour >= 12, Hour == 12
        result = mAddReport.convertTime(14, 5);
        assertEquals("2:05:00 PM", result);
    }
}
