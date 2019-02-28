package com.neelk.fbla2018;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
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
    private ShareButton realShareButton;
    private ImageView facebookButton;
    private ImageView athenaButton;
    private ImageView twitterButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupNavigation();


        databaseReference = FirebaseDatabase.getInstance().getReference();
        welcomeTextView = findViewById(R.id.welcomeTextView);
        pieChart = findViewById(R.id.pieChart);
        twitterButton = findViewById(R.id.home_twitterButton);
        facebookButton = findViewById(R.id.home_facebookSocial);
        athenaButton = findViewById(R.id.home_athenaWebsite);
        realShareButton = findViewById(R.id.home_realShareButton);

        twitterButton.setOnClickListener(twitterOnClick);
        facebookButton.setOnClickListener(facebookOnClick);
        athenaButton.setOnClickListener(athenaOnClick);

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://github.com/neelkandlikar/Athena"))
                .setQuote("I'm playing the hottest new game, Athena, come play with me by downloading the app using the link below!")
                .build();

        ShareDialog shareDialog = new ShareDialog(Home.this);
        shareDialog.canShow(content, ShareDialog.Mode.AUTOMATIC);
        realShareButton.setShareContent(content);

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
                    if (UserInfo.getName() == null) {
                        UserInfo.setName(dataSnapshot.child("UserInfo").child(UserInfo.getEmail()).child("name").getValue(String.class));
                    }

                    if (UserInfo.getGoogleSignInAccount() != null) {

                    }

                    welcomeString = "Welcome, " + UserInfo.getName();
                    setTextView(welcomeString);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else welcomeString = "Welcome, " + UserInfo.getName();


        if (welcomeString != null) {
            setTextView(welcomeString);
        }
    }

    private void setupPieChart() {


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numCorrect;
                int totalAnswered;
                try {
                    numCorrect = Integer.parseInt(dataSnapshot.child("UserInfo").child(UserInfo.getEmail()).child("numberCorrect").getValue(String.class).trim());
                    totalAnswered = Integer.parseInt(dataSnapshot.child("UserInfo").child(UserInfo.getEmail()).child("totalQuestions").getValue(String.class).trim());
                } catch (NullPointerException e) {
                    numCorrect = 0;
                    totalAnswered = 0;
                }

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
                pieChart.setCenterText("Question History");
                pieChart.setCenterTextColor(Color.WHITE);
                pieChart.setCenterTextSize(15);
                pieChart.setCenterTextTypeface(Typeface.SANS_SERIF);
                pieChart.setTransparentCircleColor(Color.BLACK);
                pieChart.setHoleColor(Color.rgb(51, 51, 51));
                pieChart.animateXY(1000, 1000);
                pieChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private View.OnClickListener facebookOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            realShareButton.performClick();

        }
    };


    private View.OnClickListener twitterOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            shareTwitter("I'm playing the hottest new game, Athena, come play with me by downloading the app using the link below!");
        }
    };


    private View.OnClickListener athenaOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String url = "http://athenaquiz.com";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    };





    private void shareTwitter(String message) {
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, message);
        tweetIntent.setType("text/plain");


        PackageManager packManager = getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            startActivity(Intent.createChooser(tweetIntent, "Share Text"));
        } else {
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, message);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(message)));
            startActivity(i);
        }
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
           // Log.wtf(TAG, "UTF-8 should always be supported", e);
            return "";
        }
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
