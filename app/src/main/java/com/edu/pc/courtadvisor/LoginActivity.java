package com.edu.pc.courtadvisor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private CardView log_in;
    private EditText login;
    private EditText password;

    // Progress Dialog
    private ProgressDialog mRegProgress;

    // Firebase
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Progress Dialog
        mRegProgress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);

        log_in = findViewById(R.id.logInButton);
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);

        log_in.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void logIn(View view) {
        String email = login.getText().toString().trim();
        String pw = password.getText().toString().trim();

        if (email.isEmpty()) {
            login.setError("Email is required");
            login.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            login.setError("Please enter a valid email");
            login.requestFocus();
            return;
        }

        if (pw.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if (pw.length() < 6) {
            password.setError("Minimum length of password is 6");
            password.requestFocus();
            return;
        }

        // Show progress dialog
        mRegProgress.setTitle("Logging in");
        mRegProgress.setMessage("Please wait while we check your credentials");
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.show();

        mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    mRegProgress.dismiss();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    mRegProgress.hide();
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    public void onGoSignUp(View view) {
        finish();
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
