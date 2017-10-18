package edu.gatech.cs2340.rattracker.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import edu.gatech.cs2340.rattracker.R;
import edu.gatech.cs2340.rattracker.model.RatReport;

public class ReportDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        RatReport report = bundle.getParcelable("RatReport");
        getSupportActionBar().setTitle(report.getDateCreated());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        populateData(report);
    }

    /**
     * Obtains and displays the specifics of a report by updating the page's TextViews
     * @param report the Rat Report to get the details from
     */
    public void populateData(RatReport report) {
        TextView date = findViewById(R.id.dateCreated);
        TextView address = findViewById(R.id.zipcode);
        TextView borough = findViewById(R.id.borough);
        TextView locType = findViewById(R.id.locType);
        TextView lat = findViewById(R.id.lat);
        TextView lng = findViewById(R.id.lng);


        date.setText("Date: " + report.getDateCreated());
        String addrString = report.getIncidentAddress() + " " + (int) report.getIncidentZip();
        address.setText("Address: " + addrString);
        borough.setText("Borough: " + report.getBorough());
        locType.setText("Location Type: " + report.getLocationType());
        lat.setText("Latitude: " + report.getLatitude());
        lng.setText("Longitude: " + report.getLongitude());
    }

}
