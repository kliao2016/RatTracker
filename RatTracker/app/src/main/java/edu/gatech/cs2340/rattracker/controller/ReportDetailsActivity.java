package edu.gatech.cs2340.rattracker.controller;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import edu.gatech.cs2340.rattracker.R;
import edu.gatech.cs2340.rattracker.model.RatReport;

/**
 * Activity that displays the details of a RatReport
 * Only accessible from the ReportListActivity
 * @author Brian Glowniak
 */
public class ReportDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            RatReport report = bundle.getParcelable("RatReport");
            if (report != null) {
                ActionBar actionbar = getSupportActionBar();
                if (actionbar != null) {
                    actionbar.setTitle(report.getDateCreated());
                    actionbar.setDisplayHomeAsUpEnabled(true);
                    actionbar.setDisplayShowHomeEnabled(true);
                }
                populateData(report);
            }
        }
    }

    /**
     * Obtains and displays the specifics of a report by updating the page's TextViews
     * @param report the Rat Report to get the details from
     */
    private void populateData(RatReport report) {
        TextView date = findViewById(R.id.dateCreated);
        TextView address = findViewById(R.id.zipcode);
        TextView borough = findViewById(R.id.borough);
        TextView locType = findViewById(R.id.locType);
        TextView lat = findViewById(R.id.lat);
        TextView lng = findViewById(R.id.lng);

        //use string variables to avoid checker internationalization issues
        String dateText = "Date: " + report.getDateCreated();
        date.setText(dateText);

        String addressString = "Address: " + report.getIncidentAddress()
                + " " + (int) report.getIncidentZip();
        address.setText(addressString);

        String boroughText = "Borough: " + report.getBorough();
        borough.setText(boroughText);

        String locTypeText = "Location Type: " + report.getLocationType();
        locType.setText(locTypeText);

        String latText = "Latitude: " + report.getLatitude();
        lat.setText(latText);

        String lngText = "Longitude: " + report.getLongitude();
        lng.setText(lngText);
    }

}
