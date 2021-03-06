package edu.gatech.cs2340.rattracker.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.format.DateFormat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import edu.gatech.cs2340.rattracker.R;
import edu.gatech.cs2340.rattracker.model.RatReport;

/**
 * Activity that provides the input fields for entering a new RatReport
 * Utilizes Google Places API and validates all input
 * @author Brian Glowniak
 */
public class AddReport extends AppCompatActivity {
    private Spinner boroughs;
    private Spinner locTypes;
    private EditText dateText;
    private EditText timeText;
    private EditText addressText;
    private Button cancelButton;
    private Button addReportButton;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_report);

        dateText = findViewById(R.id.date);
        timeText = findViewById(R.id.time);
        addressText = findViewById(R.id.address);
        cancelButton = findViewById(R.id.cancelButton);
        addReportButton = findViewById(R.id.addReportButton);
        boroughs = findViewById(R.id.boroughSpinner);
        locTypes = findViewById(R.id.locationType);

        populateSpinners();
        setClickListeners();
    }

    /**
     * Method that creates a new RatReport and then adds to the database
     * Calls validateInput() to validate the data
     * @return whether the report was successfully added
     */
    private boolean addNewReport(String address, String date, String time) {
        if (!validateInput(address, date, time)) {
            return false;
        }
        String dateCreated = date.trim() + " " + time.trim();
        String locationType = locTypes.getSelectedItem().toString();

        //extract address
        //this extraction makes certain assumptions about the input
        String[] tokenAddress = address.split(",");

        //the address we want will always be before the first comma
        String incidentAddress = tokenAddress[0].toUpperCase();
        String city = tokenAddress[1].trim();
        double incidentZip = 0;
        try {
            //end of address is zip, USA
            incidentZip = Double.parseDouble(
                    address.substring(address.length() - 10, address.length() - 5));
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Inputted address may not have a zip code. Defaulted to 0",
                    Toast.LENGTH_SHORT).show();
        }

        String borough = boroughs.getSelectedItem().toString().toUpperCase();
        double latitude = place.getLatLng().latitude;
        double longitude = place.getLatLng().longitude;

        RatReport newReport = new RatReport(dateCreated, locationType, incidentZip, incidentAddress,
                                            city, borough, latitude, longitude);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("reports");
        ref = ref.push();
        ref.setValue(newReport);
        return true;
    }

    /**
     * Displays the DatePickerFragment
     */
    private void showDatePickerDialog() {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(fm, "datePicker");
    }

    /**
     * Displays the TimePickerFragment
     */
    private void showTimePickerDialog() {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(fm, "timePicker");
    }

    /**
     * Callback function used within the DatePickerFragment to set the date text field
     * Solves the problem of defining the text as static
     * @param date the new date
     */
    private void setDateText(CharSequence date) { this.dateText.setText(date); }

    /**
     * Callback function used within the TimePickerFragment to set the time text field
     * Solves the problem of defining the text as static
     * @param time the new time
     */
    private void setTimeText(CharSequence time) { this.timeText.setText(time); }

    /**
     * Defines a fragment that is shown when choosing the date that displays a calendar to the user
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        /**
         * Method that is called upon closure of the DatePickerFragment
         * Sets the dateText field to a string in the format "MM/DD/YYYY"
         * @param view the current view
         * @param year the entered year
         * @param month the entered month
         * @param day the entered day
         */
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            AddReport activity = (AddReport) this.getActivity();
            activity.setDateText((month + 1) + "/" + day + "/" + year);
        }
    }

    /**
     * Defines a fragment that is shown when choosing the time that displays a clock to the user
     */
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        /**
         * Method that is called upon closure of the TimePickerFragment
         * Sets the timeText field to a string in the 12 hour format "H:MM:SS AM/PM"
         * @param view the current view
         * @param hourOfDay the hour entered in 24 hr format
         * @param minute the minute entered
         */
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            AddReport activity = (AddReport) this.getActivity();
            activity.setTimeText(activity.convertTime(hourOfDay, minute));
        }
    }

    /**
     * Helper method used by the TimePickerFragment to convert an hour and minute to
     * the format "H:MM:SS AM/PM"
     * @param hour the hour in 24 hour format
     * @param minute the minute
     * @return the formatted string
     */
    public String convertTime(int hour, int minute) {
        String minString = "" + minute;
        if (minute < 10) {
            minString = "0" + minString;
        }
        String am_pm;
        String hourString = "" + hour;
        if (hour < 12) {
            am_pm = "AM";
            if (hour == 0) {
                hourString = "12";
            }

        } else {
            am_pm = "PM";
            if(hour != 12) {
                hourString = "" + (hour - 12);
            }
        }
        return hourString + ":" + minString + ":" + "00" + " " + am_pm;
    }

    /**
     * Method that checks whether the inputted information for the report is valid.
     * Only needs to check address, date, and time due to the other inputs being controlled
     * @return whether the input for the report is valid
     */
    private boolean validateInput(String address, String date, String time) {
        //validate date
        boolean dateV = (date != null) && (!date.isEmpty());

        //validate time
        boolean timeV = (time != null) && (!time.isEmpty());

        //validate address
        // Since we use autocomplete, we just need to check that an address was entered
        boolean addressV = (address != null) && (!address.isEmpty());

        String toastMessage = "";
        if (!dateV) {
            toastMessage = getResources().getString(R.string.invalid_date);
//            toastMessage = "you must enter a date";
        } else if(!timeV) {
            toastMessage = getResources().getString(R.string.invalid_time);

        } else if(!addressV) {
            toastMessage = getResources().getString(R.string.invalid_address);

        }

        boolean isValid = dateV && timeV && addressV;
        if (!isValid) {
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    /**
     * Method that populates the borough and location type spinners
     */
    private void populateSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.boroughs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boroughs.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.locTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locTypes.setAdapter(adapter2);
    }

    /**
     * Generates an overlaid intent that allows the user to enter an address
     * Makes use of the Google Places API to autocomplete user input
     */
    private void generateAddressCompletion() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).setCountry("US")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setBoundsBias(new LatLngBounds(
                            new LatLng(40.5, -74.35),
                            new LatLng(40.95, -73.5)))
                            .setFilter(typeFilter).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(this,
                    "Error. Google Play Services are not up to date",
                    Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this,
                    "Error. Google Play Services unavailable",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback function upon closing an activity opened for a result
     * Used specifically for the PlaceAutocomplete intent
     * Passes on the user entered data to the add report activity
     * @param requestCode specifies what functionality was requested
     * @param resultCode specifies the result of the activity
     * @param data contains the data entered when the intent was open
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);
                addressText.setText(place.getAddress());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Toast.makeText(this,
                        "Error. Please re-enter the address",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Method that sets the click listeners for the buttons of the activity
     */
    private void setClickListeners() {
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        addressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAddressCompletion();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable address = addressText.getText();
                Editable date = dateText.getText();
                Editable time = timeText.getText();
                if (addNewReport(address.toString(), date.toString(), time.toString())) {
                    //noinspection ChainedMethodCall
                    Toast.makeText(AddReport.this,
                            "Report successfully added!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
