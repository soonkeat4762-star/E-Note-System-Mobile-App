package com.android.noteapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Removing ActionBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // Load animation
        Animation slideAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down_animation);
        // Get TextView
        TextView welcomeTextView = findViewById(R.id.welcome);
        // Start animation
        welcomeTextView.startAnimation(slideAnimation);

        // Load animation
        Animation zoomAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_animation);
        // Get TextView
        TextView descriptionTextView = findViewById(R.id.description);
        // Start animation
        descriptionTextView.startAnimation(zoomAnimation);
    }

    public void callLoginScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Login.class);

        @SuppressWarnings("unchecked")
        Pair<View, String>[] pairs = new Pair[1];
        pairs[0] = Pair.create(findViewById(R.id.login_btn), "transition_login");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
        startActivity(intent, options.toBundle());
    }

    public void callSignUpScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);

        @SuppressWarnings("unchecked")
        Pair<View, String>[] pairs = new Pair[1];
        pairs[0] = Pair.create(findViewById(R.id.signup_btn), "transition_signup");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
        startActivity(intent, options.toBundle());
    }
}


  /*/public void callSignUpScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);

        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(findViewById(R.id.signup_btn), "transition_signup_btn");
        pairs[1] = new Pair<View, String>(findViewById(R.id.screen), "transition_logo_image");
        pairs[2] = new Pair<View, String>(findViewById(R.id.login_btn), "transition_login_btn");
        pairs[3] = new Pair<View, String>(findViewById(R.id.description), "transition_layout");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }/*/
