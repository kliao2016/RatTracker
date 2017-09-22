package edu.gatech.cs2340.rattracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private Button cancelLoginButton;
    private EditText emailEditText;
    private EditText passEditText;
    private FirebaseAuth auth;
    private static boolean loginSuccess = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize fields
        loginButton = ((Button) findViewById(R.id.loginButton));
        cancelLoginButton = ((Button) findViewById(R.id.cancelLoginButton));
        emailEditText = ((EditText) findViewById(R.id.emailEditText));
        passEditText = ((EditText) findViewById(R.id.passEditText));

        // Set button actions
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(emailEditText.getText().toString(), passEditText.getText().toString());
            }
        });

        //TODO 5: Add action for cancel button here

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
    }

    /*
        Method to check if login credentials are accurate and to log users in
     */
    private void signInUser(String email, String password) {
        this.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //TODO 3: Create segue to application here
                            LoginActivity.loginSuccess = true;
                            Log.d("Login Successful", "True");
                        } else {
                            // If sign in fails, display a message to the user.
                            //TODO 4: Create popup notifying sign in failure
                            LoginActivity.loginSuccess = false;
                            Log.d("Login Successful", "False");
                        }
                    }
                });
    }

}
