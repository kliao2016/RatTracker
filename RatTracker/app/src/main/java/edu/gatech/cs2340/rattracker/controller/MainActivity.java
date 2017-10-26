package edu.gatech.cs2340.rattracker.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.gatech.cs2340.rattracker.model.User;

import edu.gatech.cs2340.rattracker.R;

public class MainActivity extends AppCompatActivity {

    private Button logoutButton;
    private Button ratReportButton;
    private Button addReportButton;
    private Button mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoutButton = (Button) findViewById(R.id.logoutButton);
        ratReportButton = (Button) findViewById(R.id.ratReportButton);
        addReportButton = findViewById(R.id.addReportButton);
        mapButton = findViewById(R.id.mapButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                goToWelcome();
                finishAffinity();
            }
        });

        ratReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToReports();
            }
        });

        addReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAdd();
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMap();
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    /**
     * Method to return to the welcome screen upon logging out
     */
    public void goToWelcome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Method to view rat reports from main screen
     */
    public void goToReports() {
        Intent intent = new Intent(this, ReportListActivity.class);
        startActivity(intent);
    }

    public void goToAdd() {
        Intent intent = new Intent(this, AddReport.class);
        startActivity(intent);
    }

    public void goToMap() {
        Intent intent = new Intent(this, RatMapActivity.class);
        startActivity(intent);
    }

}
