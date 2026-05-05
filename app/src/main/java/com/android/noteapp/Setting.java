package com.android.noteapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class Setting extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去掉ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.activity_setting, null);
        setContentView(view);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .commitNow(); // 使用 commitNow() 方法立即执行事务
    }

    // 重写BaseDrawerActivity中的方法，以确保在Setting中显示drawer
    @Override
    protected void onStart() {
        super.onStart();
        setUpNavigationDrawer();
    }

    @Override
    protected void setUpNavigationDrawer() {
        super.setUpNavigationDrawer();
        // 不再在此设置选中状态
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // Handle clicks on individual preferences
            Preference changePassword = findPreference("change_password");
            if (changePassword != null) {
                changePassword.setOnPreferenceClickListener(preference -> {
                    // 启动 ChangePasswordActivity
                    Intent changePasswordIntent = new Intent(getActivity(), ForgetPassword.class);
                    startActivity(changePasswordIntent);
                    if (getActivity() != null) {
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                    return true;
                });
            }

            Preference logout = findPreference("logout");
            if (logout != null) {
                logout.setOnPreferenceClickListener(preference -> {
                    Context context = getContext();
                    if (context != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Logout");
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                            Toast.makeText(context, "Logout successfully", Toast.LENGTH_SHORT).show();
                            // 返回到登录页面
                            Intent loginIntent = new Intent(context, Login.class);
                            // 清除之前的所有活动
                            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                        });
                        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

                        AlertDialog alertDialog = builder.create();

                        alertDialog.setOnShowListener(dialog -> {

                            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                            // 设置按钮宽度
                            int buttonWidth = 180; // 您想要的按钮宽度，单位为像素
                            LinearLayout.LayoutParams negativeButtonLayoutParams = new LinearLayout.LayoutParams(buttonWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                            LinearLayout.LayoutParams positiveButtonLayoutParams = new LinearLayout.LayoutParams(buttonWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

                            // 设置按钮外边距
                            int buttonMargin = 16; // 您想要的按钮之间的空隙，单位为像素
                            negativeButtonLayoutParams.setMargins(0, 0, buttonMargin, 0);
                            positiveButtonLayoutParams.setMargins(buttonMargin, 0, 0, 0);

                            negativeButton.setLayoutParams(negativeButtonLayoutParams);
                            positiveButton.setLayoutParams(positiveButtonLayoutParams);

                            // 更改按钮文字颜色
                            int negativeButtonTextColor = Color.WHITE; // 您想要的“否”按钮文字颜色
                            int positiveButtonTextColor = Color.WHITE; // 您想要的“是”按钮文字颜色

                            negativeButton.setTextColor(negativeButtonTextColor);
                            positiveButton.setTextColor(positiveButtonTextColor);

                            // 设置按钮背景颜色
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(Color.BLUE);
                            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(Color.BLUE);
                        });

                        alertDialog.show();
                    }

                    return true;
                });
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if ("system_notification".equals(key)) {

                boolean isNoteUpdateNotificationEnabled = sharedPreferences.getBoolean("system_notification", false);

                // 更新 FCM 的自动初始化设置
                FirebaseMessaging.getInstance().setAutoInitEnabled(isNoteUpdateNotificationEnabled);
                String notificationMessage;
                if (isNoteUpdateNotificationEnabled) {
                    notificationMessage = "System Notification enabled";
                } else {
                    notificationMessage = "System Notification is already disabled";
                }
                Toast.makeText(getContext(), notificationMessage, Toast.LENGTH_SHORT).show();

            }else if ("night_mode".equals(key)) {
                boolean isNightModeEnabled = sharedPreferences.getBoolean("night_mode", false);
                AppCompatDelegate.setDefaultNightMode(isNightModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                Activity activity = getActivity();
                if (activity != null) {
                    activity.recreate();
                }
                String nightModeMessage = isNightModeEnabled ? "Night mode enabled" : "Night mode disabled";
                Toast.makeText(getActivity(), nightModeMessage, Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public void onResume() {
            super.onResume();
            Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
