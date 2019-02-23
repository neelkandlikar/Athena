package com.neelk.fbla2018;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class ForgotPassword extends AppCompatActivity {

    private EditText email;
    private Button forgotPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = findViewById(R.id.forgotPassword_emailEditText);
        forgotPassword = findViewById(R.id.forgotPassword_forgotPasswordButton);
        forgotPassword.setOnClickListener(forgotPasswordOnClick);
    }

    private View.OnClickListener forgotPasswordOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String emailString = email.getText().toString().trim();

            if (TextUtils.isEmpty(emailString)) {
                Toasty.error(ForgotPassword.this, "please enter an email", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth = FirebaseAuth.getInstance();
            mAuth.sendPasswordResetEmail(emailString)
                    .addOnSuccessListener(ForgotPassword.this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toasty.success(ForgotPassword.this, "password reset email sent", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(ForgotPassword.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(ForgotPassword.this, "error reseting password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    };
}
