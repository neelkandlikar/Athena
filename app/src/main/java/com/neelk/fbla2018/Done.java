package com.neelk.fbla2018;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.net.Inet4Address;
import java.util.HashMap;

public class Done extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private TextView scoreTextView;
    private TextView questionsCorrectTextView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        setupNavigation();
        scoreTextView = findViewById(R.id.done_finalScore);
        questionsCorrectTextView = findViewById(R.id.done_numberCorrect);

        Intent intent = getIntent();
        int numCorrectLastGame = intent.getIntExtra("numberCorrect", 0);
        int finalScoreLastGame = intent.getIntExtra("score", 0);
        int totalQuestionsAnsweredLastGame = intent.getIntExtra("totalQuestions", 0);
        String displayFraction = numCorrectLastGame + "/" + totalQuestionsAnsweredLastGame;

        scoreTextView.setText("score" + "\n" + Integer.toString(finalScoreLastGame));
        questionsCorrectTextView.setText("total correct" + "\n" + displayFraction);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        updateData(numCorrectLastGame, totalQuestionsAnsweredLastGame);


    }

    public void selectFragment(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {

            case R.id.menu_home:
                intent = new Intent(this, Home.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;


            case R.id.menu_settings:
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }
    }

    private void setupNavigation() {
        mBottomNavigationView = findViewById(R.id.navigation_done);
        mBottomNavigationView.setSelectedItemId(R.id.menu_quiz);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

    }

    private void updateData(final int numCorrectLastGame, final int totalAnsweredLastGame) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numCorrectOld;
                int totalAnsweredOld;
                try {
                    numCorrectOld = Integer.parseInt(dataSnapshot.child("UserInfo").child(UserInfo.getEmail()).child("numberCorrect").getValue(String.class));
                } catch (NumberFormatException e) {
                    numCorrectOld = 0;
                }

                try {
                    totalAnsweredOld = Integer.parseInt(dataSnapshot.child("UserInfo").child(UserInfo.getEmail()).child("totalQuestions").getValue(String.class));
                } catch (NumberFormatException e) {
                    totalAnsweredOld = 0;
                }

                int newNumCorrect = numCorrectLastGame + numCorrectOld;
                int newTotalAnswered = totalAnsweredLastGame + totalAnsweredOld;

                writeNewValues(newNumCorrect, newTotalAnswered);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void writeNewValues(int newNumCorrect, int newTotalAnswered) {
        HashMap<String, Object> dataToAdd = new HashMap<>();
        dataToAdd.put("numberCorrect", Integer.toString(newNumCorrect));
        dataToAdd.put("totalQuestions", Integer.toString(newTotalAnswered));

        databaseReference.child("UserInfo").child(UserInfo.getEmail()).updateChildren(dataToAdd)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new AlertDialog.Builder(Done.this).setMessage("Unable to connect to server, please check your connection! Data for this game will not be saved")
                                .setNeutralButton("Ok", null).show();
                    }
                });

    }




}
