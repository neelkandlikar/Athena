package com.neelk.fbla2018;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Quiz extends AppCompatActivity implements CategoryViewAdapter.OnItemClicked {

    private ImageView fblaHistoryImageView;
    private ImageView fblaNationalOfficersImageView;
    private ImageView fblaNationalSponsorsImageView;
    private ImageView fblaNlcInfoImageView;
    private ImageView fblaIntroParliImageView;
    private BottomNavigationView mBottomNavigationView;
    private RecyclerView recyclerView;
    private ArrayList<QuizCategory> quizCategories;
    private CategoryViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        setupNavigation();
        initUi();
        initializeData();

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(Quiz.this);
        recyclerView.setLayoutManager(llm);

        mAdapter = new CategoryViewAdapter(Quiz.this, quizCategories);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClick(this);


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
//        fblaHistoryImageView = findViewById(R.id.fblaHistoryImageView);
//        fblaNationalSponsorsImageView = findViewById(R.id.nationalSponsorsImageView);
//        fblaNationalOfficersImageView = findViewById(R.id.nationalOfficersImageView);
//        fblaNlcInfoImageView = findViewById(R.id.nlcInfoImageView);
//        fblaIntroParliImageView = findViewById(R.id.parliProcedureImageView);
//
//        fblaHistoryImageView.setOnClickListener(imageOnClick);
//        fblaNationalSponsorsImageView.setOnClickListener(imageOnClick);
//        fblaNationalOfficersImageView.setOnClickListener(imageOnClick);
//        fblaNlcInfoImageView.setOnClickListener(imageOnClick);
//        fblaIntroParliImageView.setOnClickListener(imageOnClick);
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


    public class QuizCategory {

        int resId;
        String message;
        String categoryName;

        public QuizCategory(int resId, String message, String categoryName) {
            this.resId = resId;
            this.message = message;
            this.categoryName = categoryName;
        }
    }


    private void initializeData() {
        quizCategories = new ArrayList<>();
        quizCategories.add(new QuizCategory(R.drawable.stocksnew, "Explore the world of stocks and learn about the stock market!", "Stocks"));
        quizCategories.add(new QuizCategory(R.drawable.bondsnew, "There are more than just stocks when it comes to trading. Learn about bonds and mutual fund!", "Bonds"));
        quizCategories.add(new QuizCategory(R.drawable.fblahistorynew, "Test your FBLA History Knowledge!", "FBLA History"));
        quizCategories.add(new QuizCategory(R.drawable.fblaparlinew, "Improve your skills and learn about Parliamentary Procedure!", "Parliamentary Procedure"));
        quizCategories.add(new QuizCategory(R.drawable.nlcinfo, "Find out information about the FBLA National Conference!", "National Conference Info"));
        quizCategories.add(new QuizCategory(R.drawable.fblaofficersnew, "Learn about your FBLA National Officers!", "National Officers"));
        quizCategories.add(new QuizCategory(R.drawable.fblasponsorsnew, "Discover the FBLA sponsors and how they help!", "National Sponsors"));

    }

    @Override
    public void onItemClick(int position) {
        String category = quizCategories.get(position).categoryName;
        Intent intent = new Intent(Quiz.this, Game.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }


}



