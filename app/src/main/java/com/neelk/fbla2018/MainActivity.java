package com.neelk.fbla2018;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private Button emailLogin;
    private Button emailSignup;
    private static int REQUEST_CODE = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private com.shobhitpuri.custombuttons.GoogleSignInButton googleLogin;
    private static int GOOGLE_SIGN_REQUEST_CODE = 10;
    private DatabaseReference databaseReference;
    private ImageView mainLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // startActivity(new Intent(MainActivity.this, Home.class));
        mainLogo = findViewById(R.id.main_logo);
       try{
           BitmapFactory.Options options = new BitmapFactory.Options();
           options.inJustDecodeBounds = true;
           BitmapFactory.decodeResource(getResources(), R.drawable.athenalogofinal, options);

       } catch (Exception e){
           e.printStackTrace();
       }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        UserInfo.setGoogleSignInClient(mGoogleSignInClient);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        emailLogin = findViewById(R.id.mainActivity_emailButton);
        emailLogin.setOnClickListener(loginWithEmail);
        emailSignup = findViewById(R.id.mainActivity_emailSignupButton);
        emailSignup.setOnClickListener(emailSignupOnClick);

        googleLogin = findViewById(R.id.googleSignInButton);
        googleLogin.setOnClickListener(googleOnClick);


    }

    private View.OnClickListener loginWithEmail = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, LoginEmail.class));
        }
    };


    private View.OnClickListener googleOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_REQUEST_CODE);

        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_REQUEST_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.e("account println", account.getAccount().toString());
            UserInfo.setEmail(account.getEmail());
            UserInfo.setName(account.getDisplayName());
            Log.e("displayName println", account.getDisplayName());
            UserInfo.setProfilePicDownloadLink(account.getPhotoUrl());
            UserInfo.setGoogleSignInAccount(account);

            databaseReference.child("UserInfo").child(UserInfo.getEmail()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot == null || dataSnapshot.getChildren() == null) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("emailAddress", UserInfo.getEmail().replaceAll(",", "\\."));
                        map.put("name", UserInfo.getName());
                        map.put("numberCorrect", "0");
                        map.put("totalAnswered", "0");
                        databaseReference.child("UserInfo").child(UserInfo.getEmail()).setValue(map)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.error(MainActivity.this, "Error Logging In!", Toast.LENGTH_SHORT, true).show();
                                    }
                                });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            startActivity(new Intent(MainActivity.this, Home.class));

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("Error", "signInResult:failed code=" + e.getStatusCode());
            Toasty.error(MainActivity.this, "Error signing in!", Toast.LENGTH_SHORT, true).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            UserInfo.setEmail(account.getEmail());
            UserInfo.setName(account.getDisplayName());
            UserInfo.setProfilePicDownloadLink(account.getPhotoUrl());
            startActivity(new Intent(MainActivity.this, Home.class));
        }

    }

    private View.OnClickListener emailSignupOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, SignUp.class));
        }
    };
}

