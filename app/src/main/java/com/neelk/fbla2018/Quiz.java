package com.neelk.fbla2018;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Quiz extends AppCompatActivity {

    private ImageView fblaHistoryImageView;
    private ImageView fblaNationalOfficersImageView;
    private ImageView fblaNationalSponsorsImageView;
    private ImageView fblaNlcInfoImageView;
    private ImageView fblaIntroParliImageView;
    private BottomNavigationView mBottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        setupNavigation();
        initUi();


    }

    private View.OnClickListener imageOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Quiz.this, Game.class);
            intent.putExtra("id", Integer.toString(view.getId()));
            startActivity(intent);


        }
    };

    private void initUi() {
        fblaHistoryImageView = findViewById(R.id.fblaHistoryImageView);
        fblaNationalSponsorsImageView = findViewById(R.id.nationalSponsorsImageView);
        fblaNationalOfficersImageView = findViewById(R.id.nationalOfficersImageView);
        fblaNlcInfoImageView = findViewById(R.id.nlcInfoImageView);
        fblaIntroParliImageView = findViewById(R.id.parliProcedureImageView);

        fblaHistoryImageView.setOnClickListener(imageOnClick);
        fblaNationalSponsorsImageView.setOnClickListener(imageOnClick);
        fblaNationalOfficersImageView.setOnClickListener(imageOnClick);
        fblaNlcInfoImageView.setOnClickListener(imageOnClick);
        fblaIntroParliImageView.setOnClickListener(imageOnClick);
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
        mBottomNavigationView = findViewById(R.id.navigation_quiz);
        mBottomNavigationView.setSelectedItemId(R.id.menu_quiz);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

    }


}
