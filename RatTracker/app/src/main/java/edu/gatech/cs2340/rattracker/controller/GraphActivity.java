package edu.gatech.cs2340.rattracker.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.gatech.cs2340.rattracker.R;
import edu.gatech.cs2340.rattracker.model.Entry;
import edu.gatech.cs2340.rattracker.model.RatReport;

public class GraphActivity extends AppCompatActivity {

    private static EditText graphStartDate;
    private static EditText graphEndDate;
    private Button selectRangeButton;
    private static Map<String, RatReport> reportMap = new HashMap<>();
    private static List<Integer> monthBuckets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        LineChart chart = findViewById(R.id.chart);
        loadSightings();

        //Element set gives evrything, reportMap.entryset has getKey getValue, Map.Entry

        graphStartDate = findViewById(R.id.graph_start_date);
        graphEndDate = findViewById(R.id.graph_end_date);
        selectRangeButton = findViewById(R.id.graph_date_button);

        setClickListeners();
    }

    /**
     * Method to load rat sightings from database and display them on map
     */
    private void loadSightings() {

        // Load reports from reportMap
        if (!isEmpty(graphStartDate) && !isEmpty(graphEndDate)) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            Date startDate = new Date();
            Date endDate = new Date();
            try {
                startDate = formatter.parse(graphStartDate.getText().toString().trim());
                endDate = formatter.parse(graphEndDate.getText().toString().trim());
            } catch (ParseException e) {
                Log.d("Date Parse Exception", e.getMessage());
            }
            if (startDate.compareTo(endDate) > 0) {
                generateDateRangeAlert(R.string.valid_range_title,
                        R.string.valid_range_info);
                graphStartDate.setText("");
                graphEndDate.setText("");
            } else {
                monthBuckets = new ArrayList<>(endDate.getIndex());
                for (Map.Entry<String, RatReport> ratReportEntry: reportMap.entrySet()) {
                    if (ratReportEntry != null) {
                        String dateCreated = ratReportEntry.getValue().getDateCreated();
                        String dateTrimmed = dateCreated.substring(0,
                                dateCreated.indexOf(' '));
                        Date ratEntryDate = new Date();
                        try {
                            ratEntryDate = formatter.parse(dateTrimmed);
                        } catch (ParseException e) {
                            Log.d("Date Parse Exception", e.getMessage());
                        }
                        if (ratEntryDate.compareTo(startDate) >= 0
                                && ratEntryDate.compareTo(endDate) <= 0) {
                            //This section needs increment the related index in monthbuckets
                        }
                    }
                }
            }
        } else {
            for (Map.Entry<String, RatReport> ratReportEntry: reportMap.entrySet()) {
                MarkerOptions newReportOptions = new MarkerOptions()
                        .title("Sighting " + ratReportEntry.getKey())
                        .position(new LatLng(ratReportEntry.getValue().getLatitude(),
                                ratReportEntry.getValue().getLongitude()))
                        .snippet("Sighted: " + ratReportEntry.getValue()
                                .getDateCreated());
                mMap.addMarker(newReportOptions);
            }
        }
    }

    /**
     * Finds the index to increment relative to the startDate
     * @return the index to increment
     */
    private int getIndex() { //this needs to find the index related to the month and yeat
                            //for example march 2016, if its 4 months after the start month,
                            // would return 4, hasnt been implemented yet
        int index = 0;

    }

    /**
     * Defines a fragment that is shown when choosing the date that displays a calendar to the user
     */
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private boolean isStart;

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
            datePickerDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));

            // Create a new instance of DatePickerDialog and return it
            return datePickerDialog;
        }

        public void setStart(boolean start) {
            this.isStart = start;
        }

        /**
         * Method that is called upon closure of the DatePickerFragment
         * Sets the dateText field to a string in the format "MM/DD/YYYY"
         * @param view the current view
         * @param year the entered year
         * @param month the entered month
         * @param day the entered day
         */
        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (isStart) {
                graphStartDate.setText((month + 1) + "/" + day + "/" + year);
            } else {
                graphEndDate.setText((month + 1) + "/" + day + "/" + year);
            }
        }
    }


    /**
     * Method that sets the click listeners for the buttons of the activity
     */
    private void setClickListeners() {

        graphStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        graphEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

    }

    /**
     * Displays the DatePickerFragment
     * @param v the current view
     */
    public void showDatePickerDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        RatMapActivity.DatePickerFragment newFragment = new RatMapActivity.DatePickerFragment();
        if (v.getId() == R.id.start_date_text) {
            newFragment.setStart(true);
        }
        newFragment.show(fm, "datePicker");
    }

    /**
     * Method to create custom alert dialog popup
     *
     * @param title the title of the alert
     * @param message the message of the alert
     */
    private void generateDateRangeAlert(int title, int message) {
        AlertDialog.Builder loginAlertBuilder = new AlertDialog.Builder(this);
        loginAlertBuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.popup_button_okay,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int id) {
                                dialogInterface.dismiss();
                            }
                        });
        AlertDialog loginAlert = loginAlertBuilder.create();
        loginAlert.show();
    }

    /**
     * Method to check if an EditText field is empty
     *
     * @param editText the EditText field to check
     * @return true if the EditText field is empty and false otherwise
     */
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    /**
     * Inner class that is an async task that loads rat sighting data before being displayed
     */
    private class LoadSightingsTask extends AsyncTask<Void, Void, Map<String, RatReport>> {
        private ProgressBar progress;
        private Map<String, RatReport> asyncMap = new HashMap<>();

        private Query databaseRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("reports")
                .limitToLast(300);

        @Override
        protected void onPreExecute() {
            progress = (ProgressBar) findViewById(R.id.rat_map_progress_bar);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Map<String, RatReport> doInBackground(Void... voids) {
            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ratSnapshot: dataSnapshot.getChildren()) {
                        RatReport ratReport = ratSnapshot.getValue(RatReport.class);
                        if (ratReport != null) {
                            asyncMap.put(ratSnapshot.getKey(), ratReport);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return asyncMap;
        }

        @Override
        protected void onPostExecute(Map<String, RatReport> ratReports) {
            RatMapActivity.reportMap = ratReports;
            progress.setVisibility(View.GONE);
        }
    }
}
