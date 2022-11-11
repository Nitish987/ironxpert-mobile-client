package com.ironxpert.client;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ironxpert.client.common.auth.Auth;
import com.ironxpert.client.common.db.Database;
import com.ironxpert.client.utils.Promise;

import java.util.Objects;

public class MyPhotoActivity extends AppCompatActivity {
    private ImageView myPhoto;
    private ImageButton editBtn, closeBtn;
    private AppCompatButton changePhotoBtn;
    private CircularProgressIndicator changePhotoIndicator;

    private StorageReference reference;
    private boolean isPhotoPicked = false;
    private String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_photo);

        photo = getIntent().getStringExtra("PHOTO");
        reference = FirebaseStorage.getInstance().getReference("user");

        myPhoto = findViewById(R.id.photo);
        editBtn = findViewById(R.id.edit_photo);
        closeBtn = findViewById(R.id.close);
        changePhotoBtn = findViewById(R.id.change_photo_btn);
        changePhotoIndicator = findViewById(R.id.change_photo_progress);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (photo != null) {
            Glide.with(this).load(photo).into(myPhoto);
        }

        ActivityResultLauncher<Intent> pickImageResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            try {
                Intent intent = result.getData();
                photo = intent.getData().toString();
                Glide.with(getApplicationContext()).load(photo).into(myPhoto);

                isPhotoPicked = true;
                changePhotoBtn.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        editBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageResult.launch(intent);
        });

        changePhotoBtn.setOnClickListener(view -> {
            if (isPhotoPicked) {
                changePhotoBtn.setVisibility(View.INVISIBLE);
                StorageReference storageReference = reference.child("profile").child(Objects.requireNonNull(Auth.getAuthUserUid()));
                storageReference.putFile(Uri.parse(photo)).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Database.getInstance().collection("user").document(Auth.getAuthUserUid()).update("photo", uri.toString()).addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Profile pic updated.", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> {
                        changePhotoBtn.setVisibility(View.VISIBLE);
                        changePhotoIndicator.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "unable to update profile pic.", Toast.LENGTH_SHORT).show();
                    });
                })).addOnProgressListener(snapshot -> changePhotoIndicator.setVisibility(View.VISIBLE));
            } else {
                Toast.makeText(this, "Please pick a photo.", Toast.LENGTH_SHORT).show();
            }
        });

        closeBtn.setOnClickListener(view -> finish());
    }
}