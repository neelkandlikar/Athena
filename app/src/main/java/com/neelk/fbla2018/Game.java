package com.neelk.fbla2018;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

/*
 * Copyright © 2019 Neel Kandlikar. All rights reserved.
 */

public class Game extends AppCompatActivity {

    private TextView score;
    private int scoreInt = 0;
    private int numberCorrect = 0;
    private TextView question;
    private Button buttonA;
    private Button buttonB;
    private Button buttonC;
    private Button buttonD;
    private BottomNavigationView mBottomNavigationView;
    private Fragment fragment;
    private MediaPlayer correct;
    private MediaPlayer wrong;
    private ArrayList<Question> currentTopic;
    private int currentIndex;
    private CountDownTimer countDownTimer;
    private static int TIME_LEFT_MILLIS = 10000;
    private static short SMALL_TOAST_LENGTH = 1000;
    private ProgressBar progressBar;
    private boolean isBonus;
    private int timeTaken;
    private Drawable roundButton;
    private SparseArray<Button> ids = new SparseArray<>();
    private SparseArray<Button> chars = new SparseArray<>();
    private boolean stop = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setupNavigation();


        String category = getIntent().getStringExtra("category");
        score = findViewById(R.id.scoreTextView);
        question = findViewById(R.id.questionTextView);
        buttonA = findViewById(R.id.buttonA);
        buttonB = findViewById(R.id.buttonB);
        buttonC = findViewById(R.id.buttonC);
        buttonD = findViewById(R.id.buttonD);
        progressBar = findViewById(R.id.barTimer);

        correct = MediaPlayer.create(this, R.raw.correct);
        wrong = MediaPlayer.create(this, R.raw.wrong);

        roundButton = getResources().getDrawable(R.drawable.round_button, getTheme());

        ids.put(R.id.buttonA, buttonA);
        ids.put(R.id.buttonB, buttonB);
        ids.put(R.id.buttonC, buttonC);
        ids.put(R.id.buttonD, buttonD);
        chars.put('a', buttonA);
        chars.put('b', buttonB);
        chars.put('c', buttonC);
        chars.put('d', buttonD);

        initTimer();

        QuestionLoader.clearAll();
        QuestionLoader.loadQuestions(Game.this, this, category);


    }


    public void playCategory(ArrayList<Question> questions, int index) {


        buttonA.setBackgroundColor(getResources().getColor(R.color.lightRed, getTheme()));
        buttonB.setBackgroundColor(getResources().getColor(R.color.lightRed, getTheme()));
        buttonC.setBackgroundColor(getResources().getColor(R.color.lightRed, getTheme()));
        buttonD.setBackgroundColor(getResources().getColor(R.color.lightRed, getTheme()));

        buttonA.setBackground(roundButton);
        buttonB.setBackground(roundButton);
        buttonC.setBackground(roundButton);
        buttonD.setBackground(roundButton);


        if (index >= questions.size()) {
            Log.e("Timer", "playCategory Timer cancelled");
            if(countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            stop = false;
            Intent intent = new Intent(Game.this, Done.class);
            intent.putExtra("score", scoreInt);
            intent.putExtra("numberCorrect", numberCorrect);
            intent.putExtra("totalQuestions", questions.size());
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            return;
        }
        currentIndex = index;

        if (index == 0) {
            Collections.shuffle(questions);
            Collections.shuffle(questions);
            currentTopic = (ArrayList<Question>) questions.clone();
            numberCorrect = 0;
            scoreInt = 0;
        }


        score.setText("score: " + Integer.toString(scoreInt));
        if (questions.get(index).isBonusQuestion()) {
            question.setText("Tricky Question (x2 Points)" + "\n" + questions.get(index).getQuestion());
            question.setTextSize(20);
            isBonus = true;
        } else {
            question.setTextSize(25);
            question.setText(questions.get(index).getQuestion());
            isBonus = false;
        }
        buttonA.setText(questions.get(index).getOptions().get('a'));
        buttonB.setText(questions.get(index).getOptions().get('b'));
        buttonC.setText(questions.get(index).getOptions().get('c'));
        buttonD.setText(questions.get(index).getOptions().get('d'));
        char correctAnswer = questions.get(index).getCorrectAnswer();

        timeTaken = 0;
        progressBar.setProgress(100);
        startCountDownTimer();

        switch (correctAnswer) {
            case 'a':
                buttonA.setOnClickListener(correctOnClick);
                buttonB.setOnClickListener(wrongOnClick);
                buttonC.setOnClickListener(wrongOnClick);
                buttonD.setOnClickListener(wrongOnClick);
                break;

            case 'b':
                buttonA.setOnClickListener(wrongOnClick);
                buttonB.setOnClickListener(correctOnClick);
                buttonC.setOnClickListener(wrongOnClick);
                buttonD.setOnClickListener(wrongOnClick);


                break;

            case 'c':

                buttonA.setOnClickListener(wrongOnClick);
                buttonB.setOnClickListener(wrongOnClick);
                buttonC.setOnClickListener(correctOnClick);
                buttonD.setOnClickListener(wrongOnClick);

                break;

            case 'd':

                buttonA.setOnClickListener(wrongOnClick);
                buttonB.setOnClickListener(wrongOnClick);
                buttonC.setOnClickListener(wrongOnClick);
                buttonD.setOnClickListener(correctOnClick);


                break;
        }


    }

    private void newQuestion() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                playCategory(currentTopic, currentIndex + 1);
            }
        }, 1000);
    }

    private View.OnClickListener wrongOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(countDownTimer != null) {
                countDownTimer.cancel();
            }
            wrong.start();
            // Toasty.error(Game.this, "Incorrect!", SMALL_TOAST_LENGTH, true).show();
            showToastMessageError("Incorrect!", SMALL_TOAST_LENGTH);

            ids.get(view.getId()).setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
            char correctAnswer = currentTopic.get(currentIndex).getCorrectAnswer();
            chars.get(correctAnswer).setBackgroundColor(getResources().getColor(R.color.green, getTheme()));

            newQuestion();
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    playCategory(currentTopic, currentIndex + 1);
//                }
//            }, 1000);


        }
    };

    private View.OnClickListener correctOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(countDownTimer != null) {
                countDownTimer.cancel();
            }
            correct.start();
            scoreInt += calculateScore();
            numberCorrect++;

            ids.get(view.getId()).setBackgroundColor(getResources().getColor(R.color.green, getTheme()));

            showToastMessageSuccess("Correct", SMALL_TOAST_LENGTH);
            newQuestion();

//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    playCategory(currentTopic, currentIndex + 1);
//                }
//            }, 1000);

        }
    };

    public void pickCategory(String category) {


        switch (category) {
            case "FBLA History":
                playCategory(QuestionLoader.getFblaHistory(), 0);

                break;

            case "National Officers":
                playCategory(QuestionLoader.getNationalOfficerQuestions(), 0);

                break;

            case "National Sponsors":
                playCategory(QuestionLoader.getNationalSponsorQuestions(), 0);
                break;

            case "National Conference Info":
                playCategory(QuestionLoader.getNationalConferenceInfo(), 0);

                break;

            case "Parliamentary Procedure":
                playCategory(QuestionLoader.getParliQuestions(), 0);
                break;

            case "Stocks":
                playCategory(QuestionLoader.getStocks(), 0);
                break;

            case "Bonds":
                playCategory(QuestionLoader.getBonds(), 0);
                break;
        }

    }

    private void startCountDownTimer() {
        if(countDownTimer !=null) {
            countDownTimer.start();
        }
    }

    private int calculateScore() {
        int score = 10 - timeTaken;
        if (isBonus) {
            score = score * 2;
        }

        return score;

    }

    private void showToastMessageError(String text, int duration) {
        final Toast toast = Toasty.error(Game.this, text, Toast.LENGTH_SHORT, true);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }


    private void showToastMessageSuccess(String text, int duration) {
        final Toast toast = Toasty.success(Game.this, text, Toast.LENGTH_SHORT, true);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }


    public void selectFragment(MenuItem item) {

        Intent intent;

        switch (item.getItemId()) {

            case R.id.menu_home:
                intent = new Intent(this, Home.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.menu_quiz:
                intent = new Intent(this, Quiz.class);
                startActivity(intent);
                break;


            case R.id.menu_settings:
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }
    }

    private void setupNavigation() {
        mBottomNavigationView = findViewById(R.id.navigation_game);
        mBottomNavigationView.setSelectedItemId(R.id.menu_quiz);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void initTimer() {
        countDownTimer = new CountDownTimer(TIME_LEFT_MILLIS, 1000) {
            @Override
            public void onTick(long l) {
                if (stop) {
                    cancel();
                }
                progressBar.incrementProgressBy(-10);
                timeTaken++;
            }

            @Override
            public void onFinish() {

                wrong.start();
                Toasty.error(Game.this, "You have run out of time!", SMALL_TOAST_LENGTH).show();
                newQuestion();
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        playCategory(currentTopic, currentIndex + 1);
//                    }
//                }, 1000);


            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
