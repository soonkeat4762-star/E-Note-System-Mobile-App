package com.android.noteapp;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {
    private Toolbar toolbar;
    private PinView pinFromuser;
    private FirebaseAuth mAuth;
    String fullName, phoneNo, email, username, password, date, gender,whatToDo;

    String codeBySystem;

    String otp = "123456"; // 假设服务器返回的 OTP 是 "123456"
    //private FirebaseAuth mAuth;
    //private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 去掉 ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_verify_otp);
        mAuth = FirebaseAuth.getInstance();
        TextView otpDescriptionText = findViewById(R.id.otp_description_text);

        // Initialize Firebase
        //FirebaseApp.initializeApp(this);

        toolbar = findViewById(R.id.toolbarclose);
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
        toolbar.setNavigationOnClickListener(v -> closeActivityWithAnimation());

        pinFromuser = findViewById(R.id.pin_view);


        //get all data from intent
        fullName = getIntent().getStringExtra("fullName");
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        date = getIntent().getStringExtra("date");
        gender = getIntent().getStringExtra("gender");
        phoneNo = getIntent().getStringExtra("phoneNo");
        whatToDo = getIntent().getStringExtra("whatToDo");

        otpDescriptionText.setText("Enter One Time Password Sent On " + phoneNo);
        sendVerificationCodeToUser(phoneNo);

        // 获取屏幕宽度
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;

        // 计算每个格子的宽度
        int marginStart = getResources().getDimensionPixelSize(R.dimen.pin_view_margin_start);
        int marginEnd = getResources().getDimensionPixelSize(R.dimen.pin_view_margin_end);
        int availableWidth = screenWidth - marginStart - marginEnd;
        int itemWidth = availableWidth / 6; // 假设我们有6个格子

        // 设置 PinView 的 itemWidth
        pinFromuser.setItemWidth(itemWidth);

        // 初始化 FirebaseAuth 对象
        //mAuth = FirebaseAuth.getInstance();
    }

    private void sendVerificationCodeToUser(String phoneNo){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code!= null){
                        pinFromuser.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (whatToDo.equals("updateData")){
                                updateUserData();
                            } else {
                                storeNewUserData();
                            }
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerifyOTP.this,"Verification Not Completed! Try Again. ",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void updateUserData() {

        Intent intent = new Intent(getApplicationContext(), SetNewPassword.class);
        intent.putExtra("phoneNo",phoneNo);
        startActivity(intent);
        finish();
    }

    private void storeNewUserData() {

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users");

        UserHelperClass addNewUser = new UserHelperClass(fullName,username,email,phoneNo,password,date,gender);

        reference.child(phoneNo).setValue(addNewUser);

        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void closeActivityWithAnimation() {
        Animation exitAnimation = AnimationUtils.loadAnimation(this, R.anim.exit_animation);
        exitAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 在动画结束时清除相关的引用
                getWindow().getDecorView().findViewById(android.R.id.content).clearAnimation();
                VerifyOTP.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        getWindow().getDecorView().findViewById(android.R.id.content).startAnimation(exitAnimation);
    }


    public void callNextScreenFromOTP(View view) {

        String code = pinFromuser.getText().toString();
        if (!code.isEmpty()){
            verifyCode(code);
        }
    }
}

