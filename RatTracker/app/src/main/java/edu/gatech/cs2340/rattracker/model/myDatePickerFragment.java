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
import edu.gatech.cs2340.rattracker.controller.GraphActivity;

/**
 * Fragment that lets you select the dates you want to view the reports between
 */
public class myDatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private boolean isStart;
    private GraphActivity mama;

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
     * determines which of the two EditText is affected
     */
    public void setStart() {
        this.isStart = true;
    }

    /**
     * sets the GraphActivity that this fragment belongs to
     * @param mama the GraphActivity that this fragment belongs to
     */
    public void setMama(GraphActivity mama) {
        this.mama = mama;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (isStart) {
            mama.setStartDateText((month + 1) + "/" + day + "/" + year);
        } else {
            mama.setEndDateText((month + 1) + "/" + day + "/" + year);
        }
    }
}
