package com.android.noteapp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.android.noteapp.databinding.ActivityDocEditBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class DocEdit extends BaseDrawerActivity {

    ActivityDocEditBinding activityDocEditBinding;

    FloatingActionButton addNoteButton;

    pl.droidsonroids.gif.GifImageView gifImage;

    TextView txtcaption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDocEditBinding = ActivityDocEditBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(activityDocEditBinding.getRoot());

        addNoteButton = activityDocEditBinding.btnAddNote;

        // Load animation
        Animation slideAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_animation);
        // Get TextView
        txtcaption = findViewById(R.id.caption);
        // Start animation
        txtcaption.startAnimation(slideAnimation);

        // Load animation
        Animation slideAnimation_2 = AnimationUtils.loadAnimation(this, R.anim.slide_animation);
        // Get Image
        gifImage = findViewById(R.id.gif_doc);
        // Start animation
        gifImage.startAnimation(slideAnimation_2);

        requestPermissions();

        addNoteButton.setOnClickListener(v -> {
            openDrawingNoteActivity();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }



    private void requestPermissions() {
        String requiredPermission;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            requiredPermission = "android.permission.READ_MEDIA_IMAGES";
        } else {
            requiredPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        Dexter.withContext(this)
                .withPermission(requiredPermission)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // 什么都不做
                        } else {
                            requestPermissions();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void openDrawingNoteActivity() {
        Intent intent = new Intent(this, DrawingNoteActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}