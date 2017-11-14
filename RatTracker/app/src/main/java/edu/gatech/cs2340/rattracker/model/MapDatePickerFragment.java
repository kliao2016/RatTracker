package edu.gatech.cs2340.rattracker.model;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.DatePicker;

import java.util.Calendar;

import edu.gatech.cs2340.rattracker.controller.RatMapActivity;

/**
 * Created by kevinliao on 11/14/17.
 *
 * Fragment that represents a DatePicker
 */

public class MapDatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private boolean isStart;
    private RatMapActivity ratMapActivity;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                this,
                year, month, day);
        Window datePickerWindow = datePickerDialog.getWindow();
        assert datePickerWindow != null;
        datePickerWindow.setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        // Create a new instance of DatePickerDialog and return it
        return datePickerDialog;
    }

    /**
     * Method that determines which of the two EditTexts is affected
     *
     * @param isStart the boolean defining which EditText is being edited.
     *                If the start date is being edited, isStart = true and isStart = false
     *                other wise
     */
    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    /**
     * sets the RatMapActivity that this fragment belongs to
     * @param ratMapActivity the RatMapActivity that this fragment belongs to
     */
    public void setRatMapActivity(RatMapActivity ratMapActivity) {
        this.ratMapActivity = ratMapActivity;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (isStart) {
            ratMapActivity.setStartDateText((month + 1) + "/" + day + "/" + year);
        } else {
            ratMapActivity.setEndDateText((month + 1) + "/" + day + "/" + year);
        }
    }
}
