package com.neelk.fbla2018;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class SignUp extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameEditText;
    private FirebaseAuth mAuth;
    private Button signUpButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText = findViewById(R.id.signUp_nameEditText);
        signUpButton = findViewById(R.id.signup_signupButton);
        emailEditText = findViewById(R.id.signUp_emailEditText);
        passwordEditText = findViewById(R.id.signUp_passwordEditText);
        signUpButton.setOnClickListener(signUpOnClick);


    }

    private View.OnClickListener signUpOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            final String emailEditTextString = emailEditText.getText().toString().trim();
            String passwordEditTextString = passwordEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();

            if (TextUtils.isEmpty(emailEditTextString) || TextUtils.isEmpty(passwordEditTextString) || TextUtils.isEmpty(name)) {
                Toasty.error(SignUp.this, "please fill in all fields!", Toast.LENGTH_SHORT, true).show();
                // Toast.makeText(SignUp.this, "please fill in all fields!", Toast.LENGTH_SHORT).show();
                return;
            } else if (passwordEditTextString.length() < 6) {
                // Toast.makeText(SignUp.this, "password must be at least 6 characters!", Toast.LENGTH_LONG).show();
                Toasty.error(SignUp.this, "password must be at least 6 characters!", Toast.LENGTH_LONG, true).show();

                return;
            } else if (!(isEmailValid(emailEditTextString))) {
                Toasty.error(SignUp.this, "please enter a valid email!", Toast.LENGTH_SHORT, true).show();
                // Toast.makeText(SignUp.this, "please enter a valid email!", Toast.LENGTH_SHORT).show();
                return;
            }

            final String replaceEmailEditTextString = emailEditTextString.replaceAll("\\.", ",");

            mAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference();

            mAuth.createUserWithEmailAndPassword(emailEditTextString, passwordEditTextString)
                    .addOnFailureListener(SignUp.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(SignUp.this, "sign up failed, please try again!", Toast.LENGTH_SHORT, true).show();
                            //Toast.makeText(SignUp.this, "sign up failed, please try again!", Toast.LENGTH_SHORT).show();
                        }
                    });

            HashMap<String, Object> initialMap = new HashMap<>();
            initialMap.put("emailAddress", emailEditTextString);
            initialMap.put("name", name);
            databaseReference.child("UserInfo").child(replaceEmailEditTextString).setValue(initialMap)
                    .addOnFailureListener(SignUp.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(SignUp.this, "sign up failed, please try again!", Toast.LENGTH_SHORT, true).show();
                            // Toast.makeText(SignUp.this, "sign up failed, please try again!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnSuccessListener(SignUp.this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            UserInfo.setEmail(emailEditTextString);
                            Intent intent = new Intent(SignUp.this, Home.class);
                            intent.putExtra("email", replaceEmailEditTextString);
                            startActivity(intent);
                        }
                    });


        }

        boolean isEmailValid(CharSequence email) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

    };

}
