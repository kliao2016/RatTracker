package edu.gatech.cs2340.rattracker;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private Button cancelRegisterButton;
    private EditText createUsernameEditText;
    private EditText createPassEditText;
    private FirebaseAuth auth;
    private static boolean registerSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize fields
        registerButton = ((Button) findViewById(R.id.registerButton));
        cancelRegisterButton = ((Button) findViewById(R.id.cancelRegisterButton));
        createUsernameEditText = ((EditText) findViewById(R.id.createUsernameEditText));
        createPassEditText = ((EditText) findViewById(R.id.createPassEditText));

        // Set button actions
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEmpty(createUsernameEditText) && !isEmpty(createUsernameEditText)) {
                    createUserAccount(createUsernameEditText.getText().toString(),
                            createUsernameEditText.getText().toString());
                } else {
                    generateLoginAlert(R.string.emptyfield_error_title,
                            R.string.emptyfield_error_message);
                }
            }
        });

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
    }

    /*
        Method to create user account based on inputted info
     */
    private void createUserAccount(String email, String password) {
        this.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //TODO 1: Create segue to application here
                            generateLoginAlert(R.string.register_success_title,
                                               R.string.register_success_message);
                        } else {
                            // If sign in fails, display a message to the user.
                            //TODO 2: Create popup notifying sign in failure
                            generateLoginAlert(R.string.register_popup_title,
                                               R.string.register_popup_text);
                        }
                    }
                });
    }

    /*
        Create custom alert dialog popup
     */
    private void generateLoginAlert(int title, int message) {
        AlertDialog.Builder loginAlertBuilder = new AlertDialog.Builder(this);
        loginAlertBuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.popup_button_dismiss,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int id) {
                                dialogInterface.dismiss();
                            }
                        });
        AlertDialog loginAlert = loginAlertBuilder.create();
        loginAlert.show();
    }

    /*
        Check if edit text fields are empty
     */
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

}
