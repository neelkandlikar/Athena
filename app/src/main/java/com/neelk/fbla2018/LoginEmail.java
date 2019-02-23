package com.neelk.fbla2018;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class LoginEmail extends AppCompatActivity {

    private Button login;
    private Button signUp;
    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth mAuth;
    private TextView forgotPassword;
    private String passwordEditTextString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.loginEmailButton);
        login.setOnClickListener(loginOnClick);
        signUp = findViewById(R.id.signUpButton);
        signUp.setOnClickListener(signUpOnClick);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        forgotPassword = findViewById(R.id.forgotPasswordTextView);
        forgotPassword.setOnClickListener(forgotPasswordOnClick);
        mAuth = FirebaseAuth.getInstance();


    }

    private View.OnClickListener loginOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            final String emailEditTextString = emailEditText.getText().toString().trim();
            passwordEditTextString = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(emailEditTextString) || TextUtils.isEmpty(passwordEditTextString)) {
                Toasty.error(LoginEmail.this, "please fill in all fields!", Toast.LENGTH_SHORT, true).show();
               // Toast.makeText(LoginEmail.this, "please fill in all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(emailEditTextString, passwordEditTextString)
                    .addOnSuccessListener(LoginEmail.this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            UserInfo.setEmail(emailEditTextString);
                            Intent intent = new Intent(LoginEmail.this, Home.class);
                            intent.putExtra("email", emailEditTextString);
                            startActivity(intent);

                        }
                    })
                    .addOnFailureListener(LoginEmail.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                             Toasty.error(LoginEmail.this, "login failed, please try again!", Toast.LENGTH_SHORT, true).show();
                            //Toast.makeText(LoginEmail.this, "login failed, please try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    };

    private View.OnClickListener signUpOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(LoginEmail.this, SignUp.class));
        }
    };

    private View.OnClickListener forgotPasswordOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(LoginEmail.this, ForgotPassword.class));
        }
    };
}
