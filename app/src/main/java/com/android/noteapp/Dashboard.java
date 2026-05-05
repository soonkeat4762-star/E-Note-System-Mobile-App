package com.android.noteapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.noteapp.activity.NoteLive;
import com.android.noteapp.databinding.ActivityDashboardBinding;

public class Dashboard extends BaseDrawerActivity {

    private long backPressedTime;
    private Toast backToast;
    ActivityDashboardBinding activityDashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去掉ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 仅保留以下这行代码，删除setContentView(R.layout.activity_dashboard);
        setContentView(activityDashboardBinding.getRoot());
        setCheckedMenuItem(R.id.navi_dashboard);

        // Note Live 点击事件
        activityDashboardBinding.notelive.setOnClickListener(v -> {
            startActivity(new Intent(Dashboard.this, NoteLive.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 添加动画
        });

        // Most Recent 点击事件
        activityDashboardBinding.mostRecent.setOnClickListener(v -> {
            startActivity(new Intent(Dashboard.this, GlobalPDF.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 添加动画
        });

        // Upload 点击事件
        activityDashboardBinding.upload.setOnClickListener(v -> {
            startActivity(new Intent(Dashboard.this, LocalPDF.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 添加动画
        });

        // Doc Edit 点击事件
        activityDashboardBinding.editdoc.setOnClickListener(v -> {
            startActivity(new Intent(Dashboard.this, DocEdit.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // 添加动画
        });
    }


    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }


}



