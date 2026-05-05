package com.android.noteapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

public class SignUp extends AppCompatActivity {

    Button next, login;
    TextView titleText, slideText;

    TextInputLayout fullName, username, email,password;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Removing ActionBar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up);

        // Hooks

        next = findViewById(R.id.signup_next_button);
        login = findViewById(R.id.signup_login_button);
        titleText = findViewById(R.id.signup_title_text);
        slideText = findViewById(R.id.signup_slide_text);


        fullName = findViewById(R.id.signup_fullname);
        username = findViewById(R.id.signup_username);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        // 检查 getSupportActionBar() 是否为 null，以避免 NullPointerException
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        // 使用 lambda 替换匿名的 View.OnClickListener()
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        /*
        next.setOnClickListener(v ->{
            String fullname, username, email,password;

            fullname = String.valueOf(textFullName.getText());
            username = String.valueOf(textUsername.getText());
            email = String.valueOf(textEmail.getText());
            password = String.valueOf(textPassword.getText());

            if (TextUtils.isEmpty(fullname)){
                Toast.makeText(SignUp.this, "Enter Full Name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(username)){
                Toast.makeText(SignUp.this, "Enter Username", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)){
                Toast.makeText(SignUp.this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)){
                Toast.makeText(SignUp.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User();
            user.setFullname(fullname);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);

            Intent intent = new Intent(SignUp.this, SignUp2ndClass.class);
            startActivity(intent);

            VerifyOTP userData = new VerifyOTP();
            userData.useData(user);
        });

         */
    }

    @SuppressWarnings("unchecked")
    public void callNextSigupScreen(View view) {

        if (!validateFullName() | !validateUsername() | !validdateEmail() | !validdatePassword()){
            return;
        }

        String _fullName = fullName.getEditText().getText().toString().trim();
        String _email = email.getEditText().getText().toString().trim();
        String _username = username.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();

        // Your implementation for the next sign-up screen
        Intent intent = new Intent(getApplicationContext(), SignUp2ndClass.class);

        intent.putExtra("fullName" , _fullName);
        intent.putExtra("email" , _email);
        intent.putExtra("username" , _username);
        intent.putExtra("password" , _password);


        // Add Shared Animation
        Pair<View, String>[] pairs = new Pair[]{
                Pair.create(next, "transition_back_arrow_btn"),
                Pair.create(next, "transition_next_btn"),
                Pair.create(login, "transition_login_btn"),
                Pair.create(titleText, "transition_title_text"),
                Pair.create(slideText, "transition_slide_text")
        };

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp.this, pairs);
        startActivity(intent, options.toBundle());
    }

    /*
    Validation Functions
     */

    private boolean validateFullName() {
        String val =fullName.getEditText().getText().toString().trim();

        if (val.isEmpty()){
            fullName.setError("Field can not be empty");
            return false;
        }else{
            fullName.setError(null);
            fullName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateUsername() {
        String val = username.getEditText().getText().toString().trim();
        String checkspaces = "\\A\\w{1,20}\\z";

        if (val.isEmpty()) {
            username.setError("Field can not be empty");
            return false;
        }else if(val.length()>20){
            username.setError("Username is too large!");
            return false;
        } else{
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validdateEmail() {
        String val = email.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            email.setError("Invalid Email");
            return false;
        } else{
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validdatePassword() {
        String val = password.getEditText().getText().toString().trim();
        String checkPassword = "^" +
                "(?=.*[a-zA-Z])" +
                "(?=.*[@#$%^&+=])" +
                "(?=.*\\S+$)" +
                ".{4,}"+
                "$";

        if (val.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkPassword)) {
            password.setError("Password must have 4 range, 1 capital letter and 1 symbol");
            return false;
        } else{
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void callLoginFromSignUp(View view) {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}


  /*/ public void goToSignUp(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }/*/