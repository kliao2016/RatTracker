package edu.gatech.cs2340.rattracker.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

import edu.gatech.cs2340.rattracker.R;

public class AddReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_report);

        Button dateButton = findViewById(R.id.datebutton);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button addReportButton = findViewById(R.id.addReportButton);

        Spinner boroughs = findViewById(R.id.boroughSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.boroughs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boroughs.setAdapter(adapter);

        Spinner locTypes = findViewById(R.id.locationType);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.locTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locTypes.setAdapter(adapter2);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
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
                    Toast.makeText(AddReport.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showDatePickerDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(fm, "datePicker");
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
            DatePickerDialog dialog =  new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.setTitle("Set Sighting Date");
            //dialog.getDatePicker().setCalendarViewShown(false);
            //View v = getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
            //return new AlertDialog.Builder(getActivity()).setView(v).create();
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Toast.makeText(getActivity(), year + " " + month + " " + day, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput() {
        return true;
    }
}
