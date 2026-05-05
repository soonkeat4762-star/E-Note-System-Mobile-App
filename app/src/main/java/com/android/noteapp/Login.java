package com.android.noteapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.noteapp.Databases.SessionManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.j2objc.annotations.RetainedLocalRef;
import com.hbb20.CountryCodePicker;

public class Login extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    TextInputLayout phoneNumber;
    TextInputLayout password;
    Toolbar toolbar;

    CheckBox remember_me;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean isRemember;
    RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        remember_me = findViewById(R.id.remember_me);

        countryCodePicker = findViewById(R.id.login_country_code_picker);
        countryCodePicker.setCountryPreference("MY,CN,US,SG");
        phoneNumber = findViewById(R.id.login_phone_number);
        password = findViewById(R.id.passBox);
        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Set the saved phoneNumber and password if "remember" is true
        if (sharedPreferences.getBoolean("remember", false)) {
            if (phoneNumber.getEditText() != null && password.getEditText() != null) {
                phoneNumber.getEditText().setText(sharedPreferences.getString("phoneNumber", ""));
                password.getEditText().setText(sharedPreferences.getString("password", ""));
                remember_me.setChecked(true);
            }
        }

        countryCodePicker.setOnCountryChangeListener(() -> {
            String countryCode = countryCodePicker.getSelectedCountryCode();
        });

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
    }

    private boolean isConnected(Login login) {
        ConnectivityManager connectivityManager = (ConnectivityManager) login.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

        return networkCapabilities != null && (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setMessage("Please connect to the internet to proceed further")
                .setCancelable(false)
                .setPositiveButton("Connect", (dialog, i) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, i) -> {
                    startActivity(new Intent(getApplicationContext(), RetainedLocalRef.class));
                    finish();
                });
        builder.show();
    }

    public void callLogin(View view) {
        if(!isConnected(this)){
            showCustomDialog();
        }

        //validate email password
        if (!validateField()) {
            return ;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (phoneNumber.getEditText() == null || password.getEditText() == null) {
            return;
        }

        final String[] _phoneNumber = new String[1];
        _phoneNumber[0] = phoneNumber.getEditText().getText().toString().trim();

        final String _password = password.getEditText().getText().toString().trim();

        if (_phoneNumber[0].charAt(0)== '0'){
            _phoneNumber[0] = _phoneNumber[0].substring(1);
        }

        final String countryCode = countryCodePicker.getSelectedCountryCode();
        String _completePhoneNumber = "+" + countryCode + _phoneNumber[0];

        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(_completePhoneNumber);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phoneNumber.setError(null);
                    phoneNumber.setErrorEnabled(false);

                    String systemPassword = snapshot.child(_completePhoneNumber).child("password").getValue(String.class);
                    if (systemPassword != null && systemPassword.equals(_password)) {
                        password.setError(null);
                        password.setErrorEnabled(false);

                        // Save phoneNumber and password in shared preferences if "remember" is checked
                        if (remember_me.isChecked()) {
                            editor.putBoolean("remember", true);
                            editor.putString("phoneNumber", _phoneNumber[0]);
                            editor.putString("password", _password);
                            editor.apply();
                        } else {
                            editor.putBoolean("remember", false);
                            editor.putString("phoneNumber", "");
                            editor.putString("password", "");
                            editor.apply();
                        }

                        String _fullName = snapshot.child(_completePhoneNumber).child("fullName").getValue(String.class);
                        String _username = snapshot.child(_completePhoneNumber).child("username").getValue(String.class);
                        String _email = snapshot.child(_completePhoneNumber).child("email").getValue(String.class);
                        String _password = snapshot.child(_completePhoneNumber).child("password").getValue(String.class);
                        String _phoneNo = snapshot.child(_completePhoneNumber).child("phoneNo").getValue(String.class);
                        String _dateOfBirth = snapshot.child(_completePhoneNumber).child("date").getValue(String.class);
                        String _gender = snapshot.child(_completePhoneNumber).child("gender").getValue(String.class);

                        // Start Session
                        SessionManager sessionManager = new SessionManager(Login.this);
                        sessionManager.createLoginSession(_fullName,_username,_email,_password,_phoneNo,_dateOfBirth,_gender);

                        Toast.makeText(Login.this, _fullName + "\n" +_email + "\n" +_phoneNo + "\n" +_dateOfBirth,Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(Login.this, "Login successfully", Toast.LENGTH_SHORT).show();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Email does not exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callForgetPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), ForgetPassword.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // 删除 finish()
    }

    public void callSignUpFromLogin(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // 删除 finish()
    }


    private boolean validateField() {
        String _pNumber = phoneNumber.getEditText().getText().toString().trim();
        String _pass = password.getEditText().getText().toString().trim();

        if (_pNumber.isEmpty()) {
            phoneNumber.setError("Email can not be empty");
            phoneNumber.requestFocus();
            return false;
        } else if (_pass.isEmpty()) {
            password.setError("Password can not be empty");
            password.requestFocus();
            return false;
        }else {
            phoneNumber.setError("");
            password.setError("");
        }
        return true;
    }



    /*/ public void goToSignUp(View view) {
      Intent intent = new Intent(getApplicationContext(), MainActivity.class);
      startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
     }/*/

}
