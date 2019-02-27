package com.neelk.fbla2018;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import es.dmoral.toasty.Toasty;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


public class Settings extends AppCompatActivity {


    private static final int CAMERA_REQUEST = 200;
    private static final int GALLERY_REQUEST = 201;
    private BottomNavigationView mBottomNavigationView;
    private de.hdodenhof.circleimageview.CircleImageView profilePic;
    private FloatingActionButton floatingActionButton;
    private TextView nameTextView;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupNavigation();

        floatingActionButton = findViewById(R.id.floatingActionProfile);
        profilePic = findViewById(R.id.settings_profile_pic);
        floatingActionButton.setOnClickListener(changeProfilePictureButton);
        nameTextView = findViewById(R.id.settings_name);
        nameTextView.setText(UserInfo.getName());
        Button bugReportingButton = findViewById(R.id.bugReportingButton);
        bugReportingButton.setOnClickListener(bugReportOnClick);
        Button privacyPolicyButton = findViewById(R.id.privacyPolicyButton);
        privacyPolicyButton.setOnClickListener(privacyPolicyOnClick);
        Button addQuestionButton = findViewById(R.id.addQuestionButton);
        addQuestionButton.setOnClickListener(addQuestionOnClick);
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(logoutOnClick);

        storageReference = FirebaseStorage.getInstance().getReference();
        setupProfilePic();


    }


    private View.OnClickListener changeProfilePictureButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);


            AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

            builder.setPositiveButton("Take Picture", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File file = new File(Environment.getExternalStorageDirectory(), "profilePic.jpg");
                    Uri uri = FileProvider.getUriForFile(Settings.this, "neel.provider", file);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, CAMERA_REQUEST);


                }
            });
            builder.setNeutralButton("Choose from gallery", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);


                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri = null;
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            // Bundle extras = data.getExtras();


            File file = new File(Environment.getExternalStorageDirectory(), "profilePic.jpg");
            imageUri = FileProvider.getUriForFile(Settings.this, "neel.provider", file);


            profilePic.setImageURI(imageUri);


        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            InputStream inputStream;
            try {
                inputStream = getContentResolver().openInputStream(imageUri);

            } catch (FileNotFoundException e) {
                Toasty.error(Settings.this, "Error opening profile picture!", Toast.LENGTH_SHORT, true).show();
                return;
            }
            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
            profilePic.setImageBitmap(imageBitmap);
        }

        if (imageUri != null) {
            storageReference.child(UserInfo.getEmail()).child("profilePicture").putFile(imageUri)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(Settings.this, "Error updating image in database!", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }


    private void setupProfilePic() {

        if (UserInfo.getProfilePicDownloadLink() != null) {
            Picasso.get().load(UserInfo.getProfilePicDownloadLink()).into(profilePic);
            return;
        }

        storageReference.child(UserInfo.getEmail()).child("profilePicture").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePic);
                    }
                });

    }

    private View.OnClickListener bugReportOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String url = "https://github.com/neelkandlikar/Athena/issues";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    };

    private View.OnClickListener privacyPolicyOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String url = "https://github.com/neelkandlikar/Athena/blob/master/privacyPolicy.md";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    };


    private View.OnClickListener addQuestionOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(Settings.this, AddQuestion.class));
        }
    };

    private View.OnClickListener logoutOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (UserInfo.getGoogleSignInClient() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

                builder.setNeutralButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UserInfo.getGoogleSignInClient().signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(Settings.this, MainActivity.class));
                            }
                        });

                    }
                });

                builder.setPositiveButton("Remove Account", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UserInfo.getGoogleSignInClient().revokeAccess()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(Settings.this, MainActivity.class));
                                    }
                                });

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else startActivity(new Intent(Settings.this, MainActivity.class));
        }
    };

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
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

        }
    }

    private void setupNavigation() {
        mBottomNavigationView = findViewById(R.id.navigation_settings);
        mBottomNavigationView.setSelectedItemId(R.id.menu_settings);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

    }


}
