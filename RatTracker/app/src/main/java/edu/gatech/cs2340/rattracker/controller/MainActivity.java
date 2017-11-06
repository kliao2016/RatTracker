package edu.gatech.cs2340.rattracker.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import edu.gatech.cs2340.rattracker.R;

/**
 * Activity displayed after a user logs into the app
 * Acts as the central "hub" of the app
 * Allows access to all major features of the app
 * @author Team 12: The Chosen Ones
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button logoutButton = findViewById(R.id.logoutButton);
        Button ratReportButton = findViewById(R.id.ratReportButton);
        Button addReportButton = findViewById(R.id.addReportButton);
        Button mapButton = findViewById(R.id.mapButton);
        Button chartButton = findViewById(R.id.chartButton);

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

        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChart();
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    /**
     * Method to return to the welcome screen upon logging out
     */
    private void goToWelcome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Method to view rat reports from main screen
     */
    private void goToReports() {
        Intent intent = new Intent(this, ReportListActivity.class);
        startActivity(intent);
    }

    /**
     * Method to go to the add report screen
     */
    private void goToAdd() {
        Intent intent = new Intent(this, AddReport.class);
        startActivity(intent);
    }

    /**
     * Method to view the map activity
     */
    private void goToMap() {
        Intent intent = new Intent(this, RatMapActivity.class);
        startActivity(intent);
    }

    /**
     * Method to view the chart with rat data
     */
    private void goToChart() {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }
}
