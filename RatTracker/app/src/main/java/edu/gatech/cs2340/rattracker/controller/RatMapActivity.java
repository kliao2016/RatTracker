package edu.gatech.cs2340.rattracker.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
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

public class RatMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static EditText startDateText;
    private static EditText endDateText;
    private Button selectRangeButton;
    private static Map<String, RatReport> reportMap = new HashMap<>();

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
            Toast.makeText(this, "Connection to Google Maps Services successful", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Can't connect to Google Services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LoadSightingsTask task = new LoadSightingsTask();
        task.execute();

        // Center maps on geographic center of USA
        LatLng ny = new LatLng(40.713, -74.01);
        CameraUpdate zoomCamera = CameraUpdateFactory.newLatLngZoom(ny, 10);
        mMap.moveCamera(zoomCamera);
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
     * Displays the DatePickerFragment
     * @param v the current view
     */
    public void showDatePickerDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        DatePickerFragment newFragment = new DatePickerFragment();
        if (v.getId() == R.id.start_date_text) {
            newFragment.setStart(true);
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
     * Method to load rat sightings from database and display them on map
     *
     * @param googleMap the map to be displayed
     */
    private void loadSightings(GoogleMap googleMap) {
        mMap = googleMap;

        // Load reports from reportMap
        if (!isEmpty(startDateText) && !isEmpty(endDateText)) {
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

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (isStart) {
                startDateText.setText((month + 1) + "/" + day + "/" + year);
            } else {
                endDateText.setText((month + 1) + "/" + day + "/" + year);
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
