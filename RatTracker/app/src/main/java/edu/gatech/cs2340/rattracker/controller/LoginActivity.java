package edu.gatech.cs2340.rattracker.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import edu.gatech.cs2340.rattracker.R;

/**
 * Class that displays UI to handle user login
 *
 * Created by Kevin Liao on 9/15/17
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passEditText;
    private FirebaseAuth auth;
    private boolean loginSuccess;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize fields
        Button loginButton = findViewById(R.id.loginButton);
        Button cancelLoginButton = findViewById(R.id.cancelLoginButton);
        emailEditText = findViewById(R.id.emailEditText);
        passEditText = findViewById(R.id.passEditText);

        // Set button actions
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFilled(emailEditText) && isFilled(passEditText)) {
                    signInUser(emailEditText.getText().toString().trim(),
                               passEditText.getText().toString().trim());
                } else {
                    generateLoginAlert(R.string.emptyfield_error_title,
                                       R.string.emptyfield_error_message);
                }
            }
        });

        cancelLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                emailEditText.setText("");
                passEditText.setText("");
            }
        });


        // Initialize FireBase Auth
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Method to check if login credentials are accurate and to log users in
     *
     * @param email the email entered by the user
     * @param password the password entered by the user
     */
    private void signInUser(final String email, final String password) {
        this.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loginSuccess = true;
                            generateLoginAlert(R.string.login_success_title,
                                               R.string.login_success_message);
                        } else {
                            // If sign in fails, display a message to the user.
                            loginSuccess = false;
                            generateLoginAlert(R.string.login_popup_title,
                                               R.string.login_popup_text);
                        }
                    }
                });
    }

    /**
     * Method to create custom alert dialog popup
     *
     * @param title the title of the alert
     * @param message the message of the alert
     */
    private void generateLoginAlert(int title, int message) {
        AlertDialog.Builder loginAlertBuilder = new AlertDialog.Builder(this);
        loginAlertBuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.popup_button_okay,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int id) {
                                dialogInterface.dismiss();
                                if (loginSuccess) {
                                    goToMain();
                                }
                            }
                        });
        AlertDialog loginAlert = loginAlertBuilder.create();
        loginAlert.show();
    }

    /**
     * Method to check if an EditText field is empty
     *
     * @param editText the EditText field to check
     * @return true if the EditText field is empty and false otherwise
     */
    private boolean isFilled(EditText editText) {
        return editText.getText().toString().trim().length() != 0;
    }

    /**
     * Method to go to the main screen of the app upon logging in
     */
    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
