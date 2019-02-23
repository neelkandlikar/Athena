package com.neelk.fbla2018;


import android.content.Intent;
import android.graphics.Color;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private DatabaseReference databaseReference;
    private TextView welcomeTextView;
    private static String welcomeString;
    private PieChart pieChart;
    private static int timesRan = 0;
    private int[] dataSetColor = {Color.rgb(9, 148, 65), Color.rgb(229, 115, 115)};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupNavigation();


        databaseReference = FirebaseDatabase.getInstance().getReference();
        welcomeTextView = findViewById(R.id.welcomeTextView);
        pieChart = findViewById(R.id.pieChart);

        setUpWelcomeTextView();
        setupPieChart();


    }

    private void setTextView(String welcomeString) {
        welcomeTextView.setText(welcomeString);
    }


    private void setUpWelcomeTextView() {
        if (UserInfo.getName() == null) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.e("fetiching name", "name");
                    UserInfo.setName(dataSnapshot.child("UserInfo").child(UserInfo.getEmail()).child("name").getValue(String.class));
                    welcomeString = "Welcome, " + UserInfo.getName();
                    setTextView(welcomeString);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        if (welcomeString != null) {
            setTextView(welcomeString);
        }
    }

    private void setupPieChart() {


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numCorrect = Integer.parseInt(dataSnapshot.child("UserInfo").child(UserInfo.getEmail()).child("numberCorrect").getValue(String.class).trim());
                int totalAnswered = Integer.parseInt(dataSnapshot.child("UserInfo").child(UserInfo.getEmail()).child("totalQuestions").getValue(String.class).trim());

                List<PieEntry> pieEntries = new ArrayList<>();
                pieEntries.add(new PieEntry(numCorrect, "Number Correct"));
                pieEntries.add(new PieEntry(totalAnswered - numCorrect, "Number Wrong"));
                PieDataSet pieDataSet = new PieDataSet(pieEntries, "Answers Breakdown");
                pieDataSet.setColors(dataSetColor);
                pieDataSet.setValueTextSize(20);
                pieDataSet.setValueTextColor(Color.WHITE);
                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.setNoDataText("Loading Data...");
                pieChart.getLegend().setEnabled(false);
                pieChart.getDescription().setEnabled(false);
                pieChart.setHoleRadius(70);
                pieChart.setTransparentCircleColor(Color.BLACK);
                pieChart.setHoleColor(Color.rgb(51, 51, 51));
                pieChart.animateXY(1400, 1400);
                pieChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void selectFragment(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {


            case R.id.menu_quiz:
                intent = new Intent(this, Quiz.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;


            case R.id.menu_settings:
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }
    }

    private void setupNavigation() {
        mBottomNavigationView = findViewById(R.id.navigation_home);
        mBottomNavigationView.setSelectedItemId(R.id.menu_home);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

    }


}
