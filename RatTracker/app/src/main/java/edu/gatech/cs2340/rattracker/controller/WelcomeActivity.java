package edu.gatech.cs2340.rattracker.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.gatech.cs2340.rattracker.R;

public class WelcomeActivity extends AppCompatActivity {

    private Button welcomeLoginButton;
    private Button welcomeRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeLoginButton = ((Button) findViewById(R.id.welcomeLoginButton));
        welcomeRegisterButton = ((Button) findViewById(R.id.welcomeSignUp));
    }
}
