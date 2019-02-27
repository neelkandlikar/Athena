package com.neelk.fbla2018;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.games.leaderboard.Leaderboard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.URLEncoder;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TooManyListenersException;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class Done extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private TextView scoreTextView;
    private TextView questionsCorrectTextView;
    private DatabaseReference databaseReference;
    private static final String TAG = "Done:";
    private int finalScoreLastGame;
    private StorageReference storageReference;

    private de.hdodenhof.circleimageview.CircleImageView imageView1;
    private TextView name1;
    private TextView score1;
    private de.hdodenhof.circleimageview.CircleImageView imageView2;
    private TextView name2;
    private TextView score2;
    private de.hdodenhof.circleimageview.CircleImageView imageView3;
    private TextView name3;
    private TextView score3;
    private ImageView facebookShare;
    private ImageView twitterShare;
    private static int LEADERBOARD_SIZE = 3;
    private com.facebook.share.widget.ShareButton realShareButton;


    private HashMap<String, Object> leaderboardMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        setupNavigation();
        scoreTextView = findViewById(R.id.done_finalScore);
        questionsCorrectTextView = findViewById(R.id.done_numberCorrect);
        twitterShare = findViewById(R.id.twitterImageView);
        facebookShare = findViewById(R.id.facebookImageView);
        realShareButton = findViewById(R.id.realShareButton);


        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://github.com/neelkandlikar/Athena"))
                .setQuote("I just scored " + finalScoreLastGame + " points on Athena!" + "\n" + "Download the game today!")
                .build();

        ShareDialog shareDialog = new ShareDialog(Done.this);
        shareDialog.canShow(content, ShareDialog.Mode.AUTOMATIC);
        realShareButton.setShareContent(content);
        facebookShare.setOnClickListener(facebookOnClick);
        twitterShare.setOnClickListener(twitterOnClick);
        storageReference = FirebaseStorage.getInstance().getReference();


        Intent intent = getIntent();
        int numCorrectLastGame = intent.getIntExtra("numberCorrect", 0);
        finalScoreLastGame = intent.getIntExtra("score", 0);
        int totalQuestionsAnsweredLastGame = intent.getIntExtra("totalQuestions", 0);
        String displayFraction = numCorrectLastGame + "/" + totalQuestionsAnsweredLastGame;

        scoreTextView.setText("score" + "\n" + Integer.toString(finalScoreLastGame));
        questionsCorrectTextView.setText("total correct" + "\n" + displayFraction);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        initUi();
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
                loadLeaderboard();

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

    private void loadLeaderboard() {

        databaseReference.child("Leaderboard").orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                ArrayList<LeaderboardEntry> leaderboardEntries = new ArrayList<>();

                for (DataSnapshot entry : dataSnapshot.getChildren()) {
                    leaderboardEntries.add(new LeaderboardEntry(entry.getKey(), Integer.parseInt(entry.getValue(String.class))));
                }

                leaderboardEntries.sort(new Comparator<LeaderboardEntry>() {
                    @Override
                    public int compare(LeaderboardEntry l1, LeaderboardEntry l2) {
                        return l2.compareTo(l1);
                        // greatest to least
                    }
                });

                Log.e(TAG, leaderboardEntries.toString());
                Log.e(TAG,"finalScore " + finalScoreLastGame);
                boolean toUpdate = false;
                boolean entryExists = false;

                for (int i = leaderboardEntries.size() - 1; i >= 0; i--) {
                    if (leaderboardEntries.get(i).getEmail().equals(UserInfo.getEmail())) {
                        entryExists = true;
                        if (leaderboardEntries.get(i).getValue() < finalScoreLastGame) {
                            leaderboardEntries.get(i).setValue(finalScoreLastGame);
                            toUpdate = true;
                            leaderboardEntries.sort(new Comparator<LeaderboardEntry>() {
                                @Override
                                public int compare(LeaderboardEntry l1, LeaderboardEntry l2) {
                                    return l2.compareTo(l1);
                                    // greatest to least
                                }
                            });
                        }
                    }
                }
                if (!entryExists) {
                    if (finalScoreLastGame > leaderboardEntries.get(leaderboardEntries.size()-1).getValue()) {
                        toUpdate = true;
                        if (leaderboardEntries.size() == LEADERBOARD_SIZE) {
                            leaderboardEntries.remove(leaderboardEntries.size()-1);
                        }
                        for (int i = leaderboardEntries.size() - 1; i >= 0; i--) {
                            if (leaderboardEntries.get(i).getValue() < finalScoreLastGame) {
                                leaderboardEntries.add(i, new LeaderboardEntry(UserInfo.getEmail(), finalScoreLastGame));
                            }
                        }
                    } else if (leaderboardEntries.size() < LEADERBOARD_SIZE){
                        leaderboardEntries.add(leaderboardEntries.size(), new LeaderboardEntry(UserInfo.getEmail(), finalScoreLastGame));
                        toUpdate = true;
                    }
                }

                Log.e("updated", leaderboardEntries.toString());
                Log.e("toUpdate", "to update " + toUpdate);

                displayLeaderboard(leaderboardEntries, toUpdate);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void displayLeaderboard(final ArrayList<LeaderboardEntry> entries, final boolean update) {


        databaseReference.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot emails : dataSnapshot.getChildren()) {
                    for (int i = 0; i < entries.size(); i++) {
                        if (entries.get(i).getEmail().equals(emails.getKey())) {
                            leaderboardMap.put("name" + (i + 1) + "String", emails.child("name").getValue(String.class));
                        }
                    }
                }

                Log.e(TAG, leaderboardMap.toString());
                for (int i = 0; i < entries.size(); i++) {
                    int toGet = i + 1;
                    TextView name = (TextView) leaderboardMap.get("name" + toGet);
                    TextView score = (TextView) leaderboardMap.get("score" + toGet);
                    final de.hdodenhof.circleimageview.CircleImageView circleImageView = (CircleImageView) leaderboardMap.get("imageView" + (toGet));
                    String nameString = (String) leaderboardMap.get("name" + toGet + "String");
                    Log.e(TAG, "currentName " + nameString);
                    name.setText(nameString);
                    score.setText(Integer.toString(entries.get(i).getValue()));

                    storageReference.child(entries.get(i).getEmail()).child("profilePicture").getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Picasso.get().load(uri).into(circleImageView);
                                }
                            });
                }

                if (update) {
                    updateLeaderboardInDatabase(entries);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void initUi() {
        imageView1 = findViewById(R.id.profilePic1);
        imageView1.setImageResource(R.drawable.emptyprofilepic);
        score1 = findViewById(R.id.score1);
        name1 = findViewById(R.id.name1);
        leaderboardMap.put("name1", name1);
        leaderboardMap.put("score1", score1);
        leaderboardMap.put("imageView1", imageView1);


        imageView2 = findViewById(R.id.profilePic2);
        imageView2.setImageResource(R.drawable.emptyprofilepic);
        score2 = findViewById(R.id.score2);
        name2 = findViewById(R.id.name2);
        leaderboardMap.put("name2", name2);
        leaderboardMap.put("score2", score2);
        leaderboardMap.put("imageView2", imageView2);

        imageView3 = findViewById(R.id.profilePic3);
        imageView3.setImageResource(R.drawable.emptyprofilepic);
        score3 = findViewById(R.id.score3);
        name3 = findViewById(R.id.name3);
        leaderboardMap.put("name3", name3);
        leaderboardMap.put("score3", score3);
        leaderboardMap.put("imageView3", imageView3);


    }

    private void updateLeaderboardInDatabase(ArrayList<LeaderboardEntry> entries) {
        HashMap<String, Object> updatedLeaderboard = new HashMap<>();

        for (int i = 0; i < entries.size(); i++) {
            updatedLeaderboard.put(entries.get(i).getEmail(), Integer.toString(entries.get(i).getValue()));
        }

        databaseReference.child("Leaderboard").setValue(updatedLeaderboard)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        View view = findViewById(android.R.id.content);
                        Snackbar.make(view, "Congratulations, you made the Leaderboard!", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    private View.OnClickListener facebookOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            ShareLinkContent linkContent;
//            ShareDialog shareDialog;
//
//            shareDialog = new ShareDialog(Done.this);
//            if (ShareDialog.canShow(ShareLinkContent.class)) {
//                linkContent = new ShareLinkContent.Builder()
//                        .setQuote("I just scored " + finalScoreLastGame + " points playing Athena!. Download the game using the link below")
//                        .setContentUrl(Uri.parse("https://github.com/neelkandlikar/Athena"))
//                        .build();
            realShareButton.performClick();
//
//                shareDialog.show(linkContent);
//            }


        }
    };

    private View.OnClickListener twitterOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            shareTwitter("I just scored " + finalScoreLastGame + " points last game on Athena!" + "\n" + "Download it today to play with me!" + "\n" + "https://github.com/neelkandlikar/Athena");


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
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            return "";
        }
    }


}
