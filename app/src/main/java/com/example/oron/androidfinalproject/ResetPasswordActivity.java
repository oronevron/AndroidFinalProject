package com.example.oron.androidfinalproject;

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
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

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

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast toast = Toast.makeText(ResetPasswordActivity.this, getString(R.string.reset_instructions_sent), Toast.LENGTH_LONG);
                                    View toastView = toast.getView();
                                    toastView.setBackgroundResource(R.color.input_register);
                                    toast.show();


                                } else {
                                    Toast toast = Toast.makeText(ResetPasswordActivity.this, getString(R.string.reset_failed), Toast.LENGTH_LONG);
                                    View toastView = toast.getView();
                                    toastView.setBackgroundResource(R.color.input_register);
                                    toast.show();
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

}