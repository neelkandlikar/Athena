package com.neelk.fbla2018;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class QuestionLoader {


    private static ArrayList<Question> nationalOfficerQuestions = new ArrayList<>();
    private static ArrayList<Question> nationalSponsorQuestions = new ArrayList<>();
    private static ArrayList<Question> parliQuestions = new ArrayList<>();
    private static ArrayList<Question> fblaHistory = new ArrayList<>();
    private static ArrayList<Question> nationalConferenceInfo = new ArrayList<>();
    private static ArrayList<Question> stocks = new ArrayList<>();
    private static ArrayList<Question> bonds = new ArrayList<>();


    public static ArrayList<Question> getStocks() {
        return stocks;
    }

    public static ArrayList<Question> getBonds() {
        return bonds;
    }


    public static ArrayList<Question> getNationalOfficerQuestions() {
        return nationalOfficerQuestions;
    }

    public static ArrayList<Question> getNationalSponsorQuestions() {
        return nationalSponsorQuestions;
    }

    public static ArrayList<Question> getParliQuestions() {
        return parliQuestions;
    }

    public static ArrayList<Question> getFblaHistory() {
        return fblaHistory;
    }

    public static ArrayList<Question> getNationalConferenceInfo() {
        return nationalConferenceInfo;
    }


    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Questions");

    public static void loadQuestions(final Context context, final Game game, final int id) {
        databaseReference.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot categories : dataSnapshot.getChildren()) {
                    String currentCategory = categories.getKey();
                    for (DataSnapshot question : categories.getChildren()) {

                        boolean bonusQuestion;

                        if (question.getKey().equals("Bonus Question (2x points)"))
                            bonusQuestion = true;
                        else bonusQuestion = false;

                        String questionText = question.child("Question").getValue(String.class);
                        String optionA = question.child("a").getValue(String.class);
                        String optionB = question.child("b").getValue(String.class);
                        String optionC = question.child("c").getValue(String.class);
                        String optionD = question.child("d").getValue(String.class);
                        String answer = question.child("Answer").getValue(String.class);

                        Question q = new Question(questionText, currentCategory, bonusQuestion);
                        q.addOption('a', optionA);
                        q.addOption('b', optionB);
                        q.addOption('c', optionC);
                        q.addOption('d', optionD);
                        assert answer != null;
                        q.setCorrectAnswer(answer.charAt(0));

                        assert currentCategory != null;
                        switch (currentCategory) {
                            case "Basic Parliamentary Procedure":
                                parliQuestions.add(q);
                                break;

                            case "National Officers":
                                nationalOfficerQuestions.add(q);
                                break;

                            case "National Sponsors":
                                nationalSponsorQuestions.add(q);
                                break;

                            case "National Conference Information":
                                nationalConferenceInfo.add(q);
                                break;

                            case "FBLA History":
                                fblaHistory.add(q);
                                break;

                            case "Investments: Stocks":
                                stocks.add(q);
                                break;

                            case "Investments: Bonds and Mutual Funds":
                                bonds.add(q);
                                break;

                        }

                    }
                }

                game.pickCategory(id);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void clearAll() {
        nationalConferenceInfo.clear();
        nationalOfficerQuestions.clear();
        nationalSponsorQuestions.clear();
        fblaHistory.clear();
        parliQuestions.clear();
    }
}
