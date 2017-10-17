package edu.gatech.cs2340.rattracker.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import edu.gatech.cs2340.rattracker.R;
import edu.gatech.cs2340.rattracker.model.RatReport;

/**
 * Brian Glowniak 10/16/17
 */

public class AddReport extends AppCompatActivity {
    private Spinner boroughs;
    private Spinner locTypes;
    private static EditText dateText;
    private static EditText timeText;
    private EditText addrText;
    private EditText zipText;
    private Button cancelButton;
    private Button addReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_report);

        dateText = findViewById(R.id.date);
        timeText = findViewById(R.id.time);
        addrText = findViewById(R.id.address);
        zipText = findViewById(R.id.zipcode);
        cancelButton = findViewById(R.id.cancelButton);
        addReportButton = findViewById(R.id.addReportButton);
        boroughs = findViewById(R.id.boroughSpinner);
        locTypes = findViewById(R.id.locationType);

        populateSpinners();
        setClickListeners();
    }

    private void addNewReport() {
        String dateCreated = dateText.getText().toString() + " " + timeText.getText().toString();
        String locationType = locTypes.getSelectedItem().toString();
        double incidentZip = Double.parseDouble(zipText.getText().toString());
        String incidentAddress = addrText.getText().toString().trim().toUpperCase();
        String city = "New York";
        String borough = boroughs.getSelectedItem().toString().toUpperCase();
        double latitude = 0;
        double longitude = 0; //TODO: use G Maps for Lat and Lng by using address + zip

        RatReport newReport = new RatReport(dateCreated, locationType, incidentZip, incidentAddress,
                                            city, borough, latitude, longitude);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("reports");
        ref = ref.push();
        ref.setValue(newReport);
    }

    public void showDatePickerDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(fm, "datePicker");
    }

    public void showTimePickerDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(fm, "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
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

        public void onDateSet(DatePicker view, int year, int month, int day) {
            dateText.setText((month + 1) + "/" + day + "/" + year);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

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

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String minString = "" + minute;
            if (minute < 10) {
                minString = "0" + minString;
            }
            String am_pm = "";
            if (hourOfDay < 12) {
                am_pm = "AM";
                if (hourOfDay == 0) {
                    hourOfDay = 12;
                }

            } else {
                am_pm = "PM";
                if(hourOfDay != 12) {
                    hourOfDay -= 12;
                }
            }
            timeText.setText(hourOfDay + ":" + minString + ":" + "00" + " " + am_pm);
        }
    }

    /**
     * Method that checks whether the inputted information for the report is valid.
     * Only needs to check address, zip, date, and time due to the other inputs being controlled
     * @return whether the input for the report is valid
     */
    private boolean validateInput() {
        String toastMessage = "";

        //validate date
        boolean dateV = dateText.getText().toString().length() != 0;

        //validate time
        boolean timeV = timeText.getText().toString().length() != 0;

        //validate address (TODO: use Google Maps API to ensure that it is a valid address)
        boolean addressV = addrText.getText().toString().length() != 0;

        //validate zip
        boolean zipV;
        String zip = zipText.getText().toString();
        try {
            int zipNum = Integer.parseInt(zip);
            zipV = zip.length() == 5 && zipNum > 0 && zipNum <= 99999;
        } catch (NumberFormatException e) {
            zipV = false;
        }

        if (!dateV) {
            toastMessage = "Invalid input: you must enter a date";
        } else if(!timeV) {
            toastMessage = "Invalid input: you must enter a time";
        } else if(!addressV) {
            toastMessage = "Invalid address input";
        } else if (!zipV) {
            toastMessage = "Invalid zipcode input";
        }

        boolean isValid = dateV && timeV && addressV && zipV;
        if (!isValid) {
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    /**
     * Method that populates the borough and location type spinners with the corresponding string arrays
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
     * Method that sets the click listeners for the buttons of the activity
     */
    private void setClickListeners() {
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
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
                if (validateInput()) {
                    addNewReport();
                    Toast.makeText(AddReport.this, "Successfully added", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
