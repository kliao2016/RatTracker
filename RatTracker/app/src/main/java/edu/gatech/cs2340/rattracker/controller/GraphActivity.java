package edu.gatech.cs2340.rattracker.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
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
import edu.gatech.cs2340.rattracker.model.GraphSightingsTask;
import edu.gatech.cs2340.rattracker.model.RatReport;

/**
 * creates a graph to display rat sightings on a monthly interval between two time spans
 */
public class GraphActivity extends AppCompatActivity {

    private static EditText graphStartDate;
    private static EditText graphEndDate;
    private Button selectRangeButton;
    private static Map<String, RatReport> reportMap = new HashMap<>();
    private static final Map<String, Integer> graphData = new HashMap<>();
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

        //Element set gives everything, reportMap entry set has getKey getValue, Map.Entry
        graphStartDate = findViewById(R.id.graph_start_date);
        graphEndDate = findViewById(R.id.graph_end_date);
        selectRangeButton = findViewById(R.id.graph_date_button);

        //pulls ratReports from FireBase and populates reportMap with them
        GraphSightingsTask task = new GraphSightingsTask();
        task.execute();

        setClickListeners();
    }

    /**
     * getter for KyleTest JUnit test
     * @return the graphData Hash Map
     */
    public static Map<String, Integer> getGraphData() {
        return graphData;
    }

    private String[] parseGraphDates() {
        String[] importantDates;
        Editable startText = graphStartDate.getText();
        String startTextForm = startText.toString();
        Editable endText = graphEndDate.getText();
        String endTextForm = endText.toString();
        String startMonthSubString = startTextForm.substring(0, startTextForm.indexOf("/")); //"MM"
        String remainder = startTextForm.substring(
                startTextForm.indexOf("/")+1, startTextForm.length());
        String startYearSubString = remainder.substring(
                remainder.indexOf("/")+1, remainder.length()); //"yyyy"
        String endMonthSubString = endTextForm.substring(0, endTextForm.indexOf("/")); //"MM"
        remainder = endTextForm.substring(endTextForm.indexOf("/")+1, endTextForm.length());
        String endYearSubString = remainder.substring(
                remainder.indexOf("/")+1, remainder.length()); //"yyyy"

        importantDates = new String[]{startMonthSubString, startYearSubString, endMonthSubString,
            endYearSubString};
        return importantDates;
    }

    private void populateGraph(int startMonthValue, int startYearValue,
                               int totalMonthDifference) {
        final int monthsInAYear = 12;
        int currentMonthValue = startMonthValue;
        int currentYearValue = startYearValue;
        for (int i = 0; i < (totalMonthDifference + 1); i++) {
            if (currentMonthValue > monthsInAYear) {
                currentMonthValue = currentMonthValue % monthsInAYear;
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
            final float monthAdjuster = 100f;
            final int intervalSpacer = 8;
            float adjustedYear = currentYearValue % 100;
            float adjustedMonth = intervalSpacer * (currentMonthValue / monthAdjuster);
            float xValue = adjustedYear + adjustedMonth;
            Log.d("GRAPH FINAL X VALUE", Float.toString(xValue));
            Log.d("GRAPH FINAL Y VALUE", Float.toString(dataToAdd));
            entries.add(new Entry(xValue, (float)(dataToAdd)));
            currentMonthValue += 1;
        }
    }

    private void setChartValues() {
        entries = new ArrayList<>();
        String[] importantDates = parseGraphDates();
        int startMonthValue = Integer.parseInt(importantDates[0]);
        int startYearValue = Integer.parseInt(importantDates[1]);
        int endMonthValue = Integer.parseInt(importantDates[2]);
        int endYearValue = Integer.parseInt(importantDates[3]);
        final int monthsInAYear = 12;
        int totalMonthDifference = (((endYearValue - startYearValue) * monthsInAYear)
                + (endMonthValue - startMonthValue));
        populateGraph(startMonthValue, startYearValue, totalMonthDifference);
    }

    private void populateDataHashMap(Date start, Date end, SimpleDateFormat format) {
        for (Map.Entry<String, RatReport> ratReportEntry: reportMap.entrySet()) {
            if (ratReportEntry != null) {
                RatReport currentReport = ratReportEntry.getValue();
                String dateCreated = currentReport.getDateCreated();
                String dateTrimmed = dateCreated.substring(0,
                        dateCreated.indexOf(' ')); ///  MM/dd/yyyy
                Date ratEntryDate = new Date();
                try {
                    ratEntryDate = format.parse(dateTrimmed);
                } catch (ParseException e) {
                    Log.d("Date Parse Exception", e.getMessage());
                }
                //if the current report's date is within the selected months/years
                //the # of reports made during that corresponding month is incremented
                //in the graphData HashMap
                if ((ratEntryDate.compareTo(start) >= 0)
                        && (ratEntryDate.compareTo(end) <= 0)) {
                    String evenShorter = dateCreated.substring(0, 3) //"MM/yyyy"
                            + dateCreated.substring(6, dateCreated.indexOf(' '));
                    if (graphData.containsKey(evenShorter)) {
                        int oldReportCount = graphData.get(evenShorter);
                        int reportCount = (oldReportCount + 1);
                        graphData.put(evenShorter, reportCount);
                    } else {
                        graphData.put(evenShorter, 1);
                    }
                }
            }
        }
    }

    /**
     * Method to load rat sightings from database and put them into the graphData
     */
    private void loadSightings() {

        // Load reports from reportMap and use their data to manipulate graphData
        if (notEmpty(graphStartDate) && notEmpty(graphEndDate)) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            Date startDate = new Date();
            Date endDate = new Date();

            try {

                Editable startText = graphStartDate.getText();
                String startString = startText.toString();
                String startTrimmed = startString.trim();
                startDate = formatter.parse(startTrimmed);

                Editable endText = graphEndDate.getText();
                String endString = endText.toString();
                String endTrimmed = endString.trim();
                endDate = formatter.parse(endTrimmed);

            } catch (ParseException e) {
                Log.d("Date Parse Exception", e.getMessage());
            }
            if (startDate.compareTo(endDate) > 0) {
                generateDateRangeAlert();
                graphStartDate.setText("");
                graphEndDate.setText("");
            } else {
                populateDataHashMap(startDate, endDate, formatter);
                setChartValues();
                LineDataSet dataSet = new LineDataSet(entries, "Sightings");
                LineData lineData = new LineData(dataSet);
                chart.setData(lineData);
                chart.invalidate();
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
    private void showDatePickerDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        myDatePickerFragment newFragment = new myDatePickerFragment();
        if (v.getId() == R.id.graph_start_date) {
            newFragment.setStart();
        }
        newFragment.show(fm, "datePicker");
    }

    /**
     * Method to create custom alert dialog popup
     *
     */
    private void generateDateRangeAlert() {
        AlertDialog.Builder loginAlertBuilder = new AlertDialog.Builder(this);
        loginAlertBuilder.setTitle(R.string.valid_range_title);
        loginAlertBuilder.setMessage(R.string.valid_range_info);
        loginAlertBuilder.setPositiveButton(R.string.popup_button_okay,
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
    private boolean notEmpty(EditText editText) {
        Editable theText = editText.getText();
        String theString = theText.toString();
        String theTrim = theString.trim();
        return !theTrim.isEmpty();
    }

    /**
     * Defines a fragment that is shown when choosing the date that displays a calendar to the user
     */
    public static class myDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private boolean isStart;

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

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (isStart) {
                String theString = (month + 1) + "/" + day + "/" + year;
                graphStartDate.setText(theString);
            } else {
                String theString = (month + 1) + "/" + day + "/" + year;
                graphEndDate.setText(theString);
            }
        }
    }
}
