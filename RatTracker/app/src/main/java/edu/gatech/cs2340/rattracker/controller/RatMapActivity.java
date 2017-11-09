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
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.gatech.cs2340.rattracker.R;
import edu.gatech.cs2340.rattracker.model.RatReport;

/**
 * Created by Kevin Liao on 10/28/17
 *
 * Activity class that loads rat sightings onto a Google Map
 */
public class RatMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText startDateText;
    private EditText endDateText;
    private Button selectRangeButton;
    private static final double LATITUDE = 40.713;
    private static final double LONGITUDE = -74.01;
    private static final float ZOOM = 10;
    private static final int REPORTS = 300;
    private static Map<String, RatReport> reportMap = new HashMap<>();

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

        if (googleServicesAvailable()) {
            Toast.makeText(this, "Connection to Google Maps Services successful",
                    Toast.LENGTH_SHORT).show();
        }

        setClickListeners();
    }

    /**
     * Private method to check if Google services are available
     *
     * @return true if Google services are available and false otherwise
     */
    private boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog userSolvableDialog = api.getErrorDialog(this, isAvailable, 0);
            userSolvableDialog.show();
        } else {
            Toast.makeText(this, "Can't connect to Google Services",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     * The map is centered on New York City, where the rat sightings take place
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LoadSightingsTask task = new LoadSightingsTask();
        task.execute();

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
     * @param v the current view
     */
    private void showDatePickerDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        DatePickerFragment newFragment = new DatePickerFragment();
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

    private void setStartDateText(CharSequence startDate) { this.startDateText.setText(startDate); }

    private void setEndDateText(CharSequence endDate) { this.endDateText.setText(endDate); }

    /**
     * Method to load rat sightings from database and display them on map
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
     * Method to check if an EditText field is empty
     *
     * @param editText the EditText field to check
     * @return true if the EditText field is empty and false otherwise
     */
    private boolean isFilled(EditText editText) {
        // Cannot call toString().isEmpty() because it doesn't account for whitespace
        return editText.getText().toString().trim().length() != 0;
    }

    /**
     * Defines a fragment that is shown when choosing the date that displays a calendar to the user
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private boolean isStart;

        @Override
        @NonNull
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

            Window dateWindow = datePickerDialog.getWindow();
            if (dateWindow != null) {
                dateWindow.setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));
            }

            // Create a new instance of DatePickerDialog and return it
            return datePickerDialog;
        }

        /**
         * Setter for isStart boolean
         *
         * @param start the boolean to check if the selected EditText is the start date picker
         */
        public void setStart(boolean start) {
            this.isStart = start;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            RatMapActivity activity = (RatMapActivity) this.getActivity();
            if (isStart) {
                activity.setStartDateText((month + 1) + "/" + day + "/" + year);
            } else {
                activity.setEndDateText((month + 1) + "/" + day + "/" + year);
            }
        }
    }

    /**
     * Inner class that is an async task that loads rat sighting data before being displayed
     */
    private class LoadSightingsTask extends AsyncTask<Void, Void, Map<String, RatReport>> {
        private ProgressBar progress;
        private Map<String, RatReport> asyncMap;

        private final Query DATABASE = FirebaseDatabase.getInstance()
                .getReference()
                .child("reports")
                .limitToLast(REPORTS);

        @Override
        protected void onPreExecute() {
            asyncMap = new HashMap<>();
            progress = findViewById(R.id.rat_map_progress_bar);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Map<String, RatReport> doInBackground(Void... voids) {
            DATABASE.addValueEventListener(new ValueEventListener() {
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
