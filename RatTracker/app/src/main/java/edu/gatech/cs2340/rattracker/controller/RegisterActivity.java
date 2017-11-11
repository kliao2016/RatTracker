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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import edu.gatech.cs2340.rattracker.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller class related to Register page.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText createUsernameEditText;
    private EditText createPassEditText;
    private RadioGroup userAdminRadioGroup;
    private RadioButton chosenRadioButton;
    private FirebaseAuth auth;
    private static boolean registerSuccess = false;
    private DatabaseReference databaseRef;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize fields
        Button registerButton = findViewById(R.id.registerButton);
        Button cancelRegisterButton = findViewById(R.id.cancelRegisterButton);
        createUsernameEditText = findViewById(R.id.createUsernameEditText);
        createPassEditText = findViewById(R.id.createPassEditText);
        userAdminRadioGroup = findViewById(R.id.userAdminRadioGroup);

        // Set button actions
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenRadioButton = findViewById(userAdminRadioGroup.getCheckedRadioButtonId());
                if (chosenRadioButton != null) {
                    String username = createUsernameEditText.getText().toString().trim();
                    String password = createPassEditText.getText().toString().trim();
                    String isAdmin = chosenRadioButton.getText().toString().trim();
                    if (isFull(createUsernameEditText) && isFull(createPassEditText)) {
                        createUserAccount(username, password);
                        addUserToDatabase(username, password, isAdmin);
                    } else {
                        generateLoginAlert(R.string.emptyfield_error_title,
                                R.string.emptyfield_error_message);
                    }
                } else {
                    generateLoginAlert(R.string.register_popup_title,
                                       R.string.register_need_radio_message);
                }
            }
        });

        cancelRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                createUsernameEditText.setText("");
                createPassEditText.setText("");
            }
        });

        // Initialize Firebase fields
        databaseRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
    }

    /**
     * Method to create a new user account based on the passed in information
     *
     * @param email the email to be associated with the user account
     * @param password the password to be associated with the user account
     */
    private void createUserAccount(String email, String password) {
        this.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            setRegisterSuccess(true);
                            generateLoginAlert(R.string.register_success_title,
                                    R.string.register_success_message);
                        } else {
                            // If sign in fails, display a message to the user.
                            setRegisterSuccess(false);
                            generateLoginAlert(R.string.register_popup_title,
                                    R.string.register_popup_text);
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
                                if (registerSuccess) {
                                    goToLogin();
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
     * @return true if the EditText field is empty, otherwise false
     */
    private boolean isFull(EditText editText) {
        return !editText.getText().toString().trim().isEmpty();
    }

    /**
     * Method to go to the login activity and end the current activity
     */
    private void goToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private static void setRegisterSuccess(boolean succ) {
        registerSuccess = succ;
    }

    private void addUserToDatabase(String email, String password, String isAdmin) {
        Map<String, Object> userValues = new HashMap<>();
        userValues.put("email", email);
        userValues.put("password", password);
        String userId = firebaseUser.getUid();
        if (chosenRadioButton != null) {
            if ("User".equals(isAdmin)) {
                userValues.put("admin", "false");
                databaseRef.child("users").child(userId).updateChildren(userValues,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError,
                                                   DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    generateLoginAlert(R.string.register_popup_title,
                                            R.string.register_popup_text);
                                }
                            }
                        });
            } else {
                userValues.put("admin", "true");
                databaseRef.child("users").child(userId).updateChildren(userValues,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError,
                                                   DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    generateLoginAlert(R.string.register_popup_title,
                                            R.string.register_popup_text);
                                }
                            }
                        });
            }

        }
    }

}
