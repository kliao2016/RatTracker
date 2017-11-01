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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.gatech.cs2340.rattracker.R;
import edu.gatech.cs2340.rattracker.model.RatReport;

public class GraphActivity extends AppCompatActivity {

    private static EditText graphStartDate;
    private static EditText graphEndDate;
    private Button selectRangeButton;
    private static Map<String, RatReport> reportMap = new HashMap<>();
    private static Map<String, Integer> graphData = new HashMap<>();
    private LineChart chart;
    private List<Entry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        chart = findViewById(R.id.chart);
        chart.setNoDataText("Have you considered selecting a date?");
        chart.setDrawBorders(true);
        chart.setBorderWidth(4);

        //Element set gives everything, reportMap.entryset has getKey getValue, Map.Entry
        graphStartDate = findViewById(R.id.graph_start_date);
        graphEndDate = findViewById(R.id.graph_end_date);
        selectRangeButton = findViewById(R.id.graph_date_button);

        //pulls ratReports from FireBase and populates reportMap with them
        LoadSightingsTask task = new LoadSightingsTask();
        task.execute();

        setClickListeners();
    }

    /**
     * Method to load rat sightings from database and put them into the graphData
     */
    private void loadSightings() {

        // Load reports from reportMap and use their data to manipulate graphData
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
                for (Map.Entry<String, RatReport> ratReportEntry: reportMap.entrySet()) {
                    if (ratReportEntry != null) {
                        String dateCreated = ratReportEntry.getValue().getDateCreated();
                        String dateTrimmed = dateCreated.substring(0,
                                dateCreated.indexOf(' ')); ///  MM/dd/yyyy
                        Date ratEntryDate = new Date();
                        try {
                            ratEntryDate = formatter.parse(dateTrimmed);
                        } catch (ParseException e) {
                            Log.d("Date Parse Exception", e.getMessage());
                        }
                        //if the current report's date is within the selected months/years
                        //the # of reports made during that corresponding month is incremented
                        //in the graphData HashMap
                        if (ratEntryDate.compareTo(startDate) >= 0
                                && ratEntryDate.compareTo(endDate) <= 0) {
                            String evenShorter = dateCreated.substring(0, 3) //"MM/yyyy"
                                    + dateCreated.substring(6, dateCreated.indexOf(' '));
                            if (graphData.containsKey(evenShorter)) {
                                int oldReportCount = graphData.get(evenShorter);
                                int reportCount = (oldReportCount += 1);
                                graphData.put(evenShorter, reportCount);
                            } else {
                                graphData.put(evenShorter, 1);
                            }
                        }
                    }
                }
                ///LOGIC FOR USING GRAPHDATA HASHMAP TO MANIPULATE VALUES OF LINECHART
                entries = new ArrayList<>();
                String startTextForm = graphStartDate.getText().toString();
                String endTextForm = graphEndDate.getText().toString();
                String startMonthSubString = startTextForm.substring(0, startTextForm.indexOf("/")); //"MM"
                int startMonthValue = Integer.parseInt(startMonthSubString);
                String remainder = startTextForm.substring(startTextForm.indexOf("/")+1, startTextForm.length());
                String startYearSubString = remainder.substring(remainder.indexOf("/")+1, remainder.length()); //"yyyy"
                int startYearValue = Integer.parseInt(startYearSubString);
                String endMonthSubString = endTextForm.substring(0, endTextForm.indexOf("/")); //"MM"
                int endMonthValue = Integer.parseInt(endMonthSubString);
                remainder = endTextForm.substring(endTextForm.indexOf("/")+1, endTextForm.length());
                String endYearSubString = remainder.substring(remainder.indexOf("/")+1, remainder.length()); //"yyyy"
                int endYearValue = Integer.parseInt(endYearSubString);
                int totalMonthDifference = (((endYearValue - startYearValue) * 12)
                        + (endMonthValue - startMonthValue));
                int currentMonthValue = startMonthValue;
                int currentYearValue = startYearValue;
                for (int i = 0; i < totalMonthDifference + 1; i++) {
                    if (currentMonthValue > 12) {
                        currentMonthValue = currentMonthValue % 12;
                        currentYearValue += 1;
                        Log.d("GRAPH Incrementing", Integer.toString(currentYearValue));
                    }
                    String currentMonthString = Integer.toString(currentMonthValue);
                    String currentYearString = Integer.toString(currentYearValue);
                    String currentDateString = currentMonthString + "/" + currentYearString; //MM/yyyy
                    int dataToAdd;
                    if (graphData.containsKey(currentDateString)) {
                        dataToAdd = graphData.get(currentDateString);
                    } else {
                        dataToAdd = 0;
                    }
                    float adjustedYear = ((float)currentYearValue) / 10000f;
                    float xValue = currentMonthValue + adjustedYear;
                    Log.d("GRAPH FINAL X VALUE", Float.toString(xValue));
                    Log.d("GRAPH FINAL Y VALUE", Float.toString(dataToAdd));
                    entries.add(new Entry(xValue, (float)(dataToAdd)));
                    currentMonthValue += 1;
                }
            }
            LineDataSet dataSet = new LineDataSet(entries, "Sightings");
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate();
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

        selectRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphData.clear();
                loadSightings();
            }
        });

    }

    /**
     * Displays the DatePickerFragment
     * @param v the current view
     */
    public void showDatePickerDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        myDatePickerFragment newFragment = new myDatePickerFragment();
        if (v.getId() == R.id.graph_start_date) {
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
     * Defines a fragment that is shown when choosing the date that displays a calendar to the user
     */
    public static class myDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
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

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (isStart) {
                graphStartDate.setText((month + 1) + "/" + day + "/" + year);
            } else {
                graphEndDate.setText((month + 1) + "/" + day + "/" + year);
            }
        }
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
            progress = (ProgressBar) findViewById(R.id.graph_progress_bar);
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
            reportMap = ratReports;
            progress.setVisibility(View.GONE);
        }
    }
}
