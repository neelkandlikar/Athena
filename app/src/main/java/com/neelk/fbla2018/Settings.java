package com.neelk.fbla2018;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

import es.dmoral.toasty.Toasty;


public class Settings extends AppCompatActivity {


    private static final int CAMERA_REQUEST = 200;
    private static final int GALLERY_REQUEST = 201;
    private BottomNavigationView mBottomNavigationView;
    private de.hdodenhof.circleimageview.CircleImageView profilePic;
    private FloatingActionButton floatingActionButton;
    private Bitmap bitmap;
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
        storageReference = FirebaseStorage.getInstance().getReference();


    }


    private View.OnClickListener changeProfilePictureButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.CAMERA}, 0);
            ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);

            builder.setPositiveButton("Take Picture", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri = null;
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            profilePic.setImageBitmap(bitmap);
            imageUri = getImageUri(Settings.this, bitmap);
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


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
