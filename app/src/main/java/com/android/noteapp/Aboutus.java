package com.android.noteapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.noteapp.databinding.ActivityAboutusBinding;

public class Aboutus extends BaseDrawerActivity {

    ActivityAboutusBinding activityAboutusBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAboutusBinding = ActivityAboutusBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去掉ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(activityAboutusBinding.getRoot());

        // 给按钮添加点击事件
        activityAboutusBinding.contactUsButton.setOnClickListener(view -> callPhoneNumber());
        activityAboutusBinding.contactEmailTextView.setOnClickListener(view -> sendEmail());
        activityAboutusBinding.whatappsIcon.setOnClickListener(view -> openWhatsapp());
        activityAboutusBinding.instagramIcon.setOnClickListener(view -> openInstagram());
        activityAboutusBinding.facebookIcon.setOnClickListener(view -> openFacebook());

        // 添加标题动画
        Animation titleAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down_animation);
        activityAboutusBinding.titleTextView.startAnimation(titleAnimation);

        // 添加Logo动画
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_animation);
        activityAboutusBinding.logoImageView.startAnimation(logoAnimation);

        // 添加按钮动画
        Animation slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_animation);
        activityAboutusBinding.contactUsButton.startAnimation(slideDownAnimation);
        activityAboutusBinding.contactUsButton.setVisibility(View.VISIBLE);
    }

        // 处理拨打电话按钮点击事件
    private void callPhoneNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+601116157870"));
        startActivity(intent);
    }

    // 处理发送电子邮件按钮点击事件
    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:NOTE.BSK.ACC@GMAIL.COM"));
        startActivity(intent);
    }

    // 处理打开Whatsapp按钮点击事件
    private void openWhatsapp() {
        String  phoneNumber = "+601116157870";
        String url = "https://api.whatsapp.com/send?phone=" +  phoneNumber;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    // 处理打开Instagram按钮点击事件
    private void openInstagram() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.instagram.com/note_bsk/"));
        startActivity(intent);
    }

    // 处理打开Facebook按钮点击事件
    private void openFacebook() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.facebook.com/NoteBSK2023/ "));
        startActivity(intent);
    }

}