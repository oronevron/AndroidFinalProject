package com.example.oron.androidfinalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // If mail wasn't entered
                if (TextUtils.isEmpty(email)) {
                    inputEmail.requestFocus();
                    inputEmail.setError(getResources().getString(R.string.enter_mail));
                    return;
                }
                else{
                    inputEmail.setError(null);
                }

                // If mail isn't in the appropriate pattern
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.requestFocus();
                    inputEmail.setError(getResources().getString(R.string.bad_mail_format));
                    return;
                }
                else{
                    inputEmail.setError(null);
                }

                // If password wasn't entered
                if (TextUtils.isEmpty(password)) {
                    inputPassword.requestFocus();
                    inputPassword.setError(getResources().getString(R.string.enter_password));
                    return;
                }
                else{
                    inputPassword.setError(null);
                }

                // If password is too short
                if (password.length() < 6) {
                    inputPassword.requestFocus();
                    inputPassword.setError(getResources().getString(R.string.short_password));
                    return;
                }
                else{
                    inputPassword.setError(null);
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast toastFail = Toast.makeText(SignupActivity.this, getString(R.string.register_failure), Toast.LENGTH_LONG);
                                    View toastViewFail = toastFail.getView();
                                    toastViewFail.setBackgroundResource(R.color.input_register);
                                    toastFail.show();
                                } else {

                                    Toast toastSuccess = Toast.makeText(SignupActivity.this, getString(R.string.register_success), Toast.LENGTH_LONG);
                                    View toastViewSuccess = toastSuccess.getView();
                                    toastViewSuccess.setBackgroundResource(R.color.input_register);
                                    toastSuccess.show();

                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
