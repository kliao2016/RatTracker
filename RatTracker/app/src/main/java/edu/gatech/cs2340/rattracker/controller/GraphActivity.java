package edu.gatech.cs2340.rattracker.controller;

import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.gatech.cs2340.rattracker.R;
import edu.gatech.cs2340.rattracker.model.Entry;
import edu.gatech.cs2340.rattracker.model.RatReport;

public class GraphActivity extends AppCompatActivity {

    private static EditText graphStartDate;
    private static EditText graphEndDate;
    private Button selectRangeButton;
    private static Map<String, RatReport> reportMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        LineChart chart = findViewById(R.id.chart);
        RatReport[] reports; //pull data from firebase with an array of reports
        List<Entry> entries = new ArrayList<Entry>();
        for (RatReport report : reports) {
            entries.add(new Entry(report.getValueX(), getValueY());
        }

        graphStartDate = findViewById(R.id.graph_start_date);
        graphEndDate = findViewById(R.id.graph_end_date);
        selectRangeButton = findViewById(R.id.graph_date_button);
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
                startDate = formatter.parse(startDateText.getText().toString().trim());
                endDate = formatter.parse(endDateText.getText().toString().trim());
            } catch (ParseException e) {
                Log.d("Date Parse Exception", e.getMessage());
            }
            if (startDate.compareTo(endDate) > 0) {
                generateDateRangeAlert(R.string.valid_range_title,
                        R.string.valid_range_info);
                startDateText.setText("");
                endDateText.setText("");
            } else {
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
}
