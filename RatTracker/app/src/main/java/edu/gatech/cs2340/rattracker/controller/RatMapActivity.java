package edu.gatech.cs2340.rattracker.controller;

import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.gatech.cs2340.rattracker.R;
import edu.gatech.cs2340.rattracker.model.LoadSightingsTask;
import edu.gatech.cs2340.rattracker.model.MapDatePickerFragment;
import edu.gatech.cs2340.rattracker.model.RatReport;

/**
 * Created by Kevin Liao on 10/28/17
 *
 * Activity class that loads rat sightings onto a Google Map
 */
public class RatMapActivity extends FragmentActivity implements OnMapReadyCallback {

    // Constant variables
    private static final double LATITUDE = 40.713;
    private static final double LONGITUDE = -74.01;
    private static final float ZOOM = 10;

    private GoogleMap mMap;
    private EditText startDateText;
    private EditText endDateText;
    private Button selectRangeButton;
    private final Map<String, RatReport> reportMap = new HashMap<>();

    /**
     * Getter to return the start date
     *
     * @return the the start date of date range
     */
    public EditText getStartDateText() {
        return this.startDateText;
    }

    /**
     * Getter to return the end date
     *
     * @return the the end date of date range
     */
    public EditText getEndDateText() {
        return this.endDateText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rat_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.rat_map_fragment);
        mapFragment.getMapAsync(this);

        startDateText = findViewById(R.id.start_date_text);
        endDateText = findViewById(R.id.end_date_text);
        selectRangeButton = findViewById(R.id.select_date_button);

        testGoogleServicesAvailable();
        setClickListeners();
    }

    /**
     * Private method to check if Google services are available
     * <p>
     * Displays a popup dialog if Google services are not available
     */
    private void testGoogleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (api.isUserResolvableError(isAvailable)) {
            Dialog userSolvableDialog = api.getErrorDialog(this, isAvailable, 0);
            userSolvableDialog.show();
        } else {
            Toast.makeText(this, "Can't connect to Google Services",
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * <p>
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * <p>
     * The map is centered on New York City, where the rat sightings take place
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Map<String, RatReport>[] asyncParams = (Map<String, RatReport>[]) new Map[1];
        asyncParams[0] = reportMap;
        LoadSightingsTask asyncTask = new LoadSightingsTask();
        asyncTask.execute(asyncParams);

        // Center maps on geographic center of NY
        LatLng ny = new LatLng(LATITUDE, LONGITUDE);
        CameraUpdate zoomCamera = CameraUpdateFactory.newLatLngZoom(ny, ZOOM);
        mMap.moveCamera(zoomCamera);
    }

    /**
     * Method to create custom alert dialog popup if the date range is invalid
     */
    private void generateDateRangeAlert() {
        AlertDialog.Builder loginAlertBuilder = new AlertDialog.Builder(this);
        loginAlertBuilder.setTitle(R.string.valid_range_title)
                .setMessage(R.string.valid_range_info)
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
     * Displays the DatePickerFragment
     *
     * @param v the current view
     */
    private void showDatePickerDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        MapDatePickerFragment newFragment = new MapDatePickerFragment();
        newFragment.setRatMapActivity(this);
        if (v.getId() == R.id.start_date_text) {
            newFragment.setStart(true);
        } else {
            newFragment.setStart(false);
        }
        newFragment.show(fm, "datePicker");
    }

    /**
     * Method that sets the click listeners for the buttons of the activity
     */
    private void setClickListeners() {
        selectRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                loadSightings(mMap);
            }
        });

        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
    }

    /**
     * Method to set the starting date of date range
     *
     * @param startDate the start date of the date range
     */
    public void setStartDateText(CharSequence startDate) {
        this.startDateText.setText(startDate);
    }

    /**
     * Method to set the ending date of the date range
     *
     * @param endDate the end date of the date range
     */
    public void setEndDateText(CharSequence endDate) {
        this.endDateText.setText(endDate);
    }

    /**
     * Method to load rat sightings from database and display them on map
     *
     * @param googleMap the map to be displayed
     */
    private void loadSightings(GoogleMap googleMap) {
        mMap = googleMap;

        // Load reports from reportMap
        if (isFilled(startDateText) && isFilled(endDateText)) {
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
                generateDateRangeAlert();
                startDateText.setText("");
                endDateText.setText("");
            } else {
                loadPinsFromRange(reportMap, startDate, endDate, formatter);
            }
        } else {
            loadPinsWithoutRange(reportMap);
        }
    }

    /**
     * Method to load pins from a date range onto the Google Map
     *
     * @param reportMap the map containing info of all the rat sightings
     * @param startDate the starting date of the date range
     * @param endDate   the ending date of the date range
     * @param formatter the formatter to convert a string to a date
     */
    private void loadPinsFromRange(Map<String, RatReport> reportMap,
                                   Date startDate,
                                   Date endDate,
                                   SimpleDateFormat formatter) {
        for (Map.Entry<String, RatReport> ratReportEntry : reportMap.entrySet()) {
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
                if ((ratEntryDate.compareTo(startDate) >= 0)
                        && (ratEntryDate.compareTo(endDate) <= 0)) {
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

    /**
     * Function to load pins from map without a specified date range
     *
     * @param reportMap the map containing information of all rat sightings
     */
    private void loadPinsWithoutRange(Map<String, RatReport> reportMap) {
        if (reportMap != null) {
            for (Map.Entry<String, RatReport> ratReportEntry : reportMap.entrySet()) {
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
     * Method to check if an EditText field is empty
     *
     * @param editText the EditText field to check
     * @return true if the EditText field is empty and false otherwise
     */
    private boolean isFilled(EditText editText) {
        // Cannot call toString().isEmpty() because it doesn't account for whitespace
        return editText.getText().toString().trim().isEmpty();
    }

}
