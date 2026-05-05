package com.android.noteapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.android.noteapp.databinding.ActivityFeedbackBinding;

public class Feedback extends BaseDrawerActivity {

    ActivityFeedbackBinding activityFeedbackBinding;

    TextView tvFeedback;
    RatingBar rbStars;

    private final ActivityResultLauncher<Intent> sendEmailLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> showSuccessDialog());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFeedbackBinding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(activityFeedbackBinding.getRoot());

        tvFeedback = findViewById(R.id.tvFeedback);
        rbStars = findViewById(R.id.rbStars);

        rbStars.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            int ratingInt = (int) rating;
            switch (ratingInt) {
                case 0:
                    tvFeedback.setText(getString(R.string.very_dissatisfied));
                    break;
                case 1:
                    tvFeedback.setText(getString(R.string.dissatisfied));
                    break;
                case 2:
                case 3:
                    tvFeedback.setText(getString(R.string.ok));
                    break;
                case 4:
                    tvFeedback.setText(getString(R.string.satisfied));
                    break;
                case 5:
                    tvFeedback.setText(getString(R.string.very_satisfied));
                    break;
                default:
                    // handle other cases
                    break;
            }
        });
    }

    public void sendEmail(View view) {
        int ratingInt = (int) rbStars.getRating();
        String ratingText;

        switch (ratingInt) {
            case 0:
                ratingText = getString(R.string.very_dissatisfied);
                break;
            case 1:
                ratingText = getString(R.string.dissatisfied);
                break;
            case 2:
            case 3:
                ratingText = getString(R.string.ok);
                break;
            case 4:
                ratingText = getString(R.string.satisfied);
                break;
            case 5:
                ratingText = getString(R.string.very_satisfied);
                break;
            default:
                ratingText = getString(R.string.unknown);
                break;
        }

        // 获取用户在EditText中输入的文本
        EditText etFeedback = findViewById(R.id.et_feedback);
        String userFeedback = etFeedback.getText().toString();

        // 将用户输入的自定义反馈添加到电子邮件的消息内容中
        String message = "User feedback:\n\n" + tvFeedback.getText().toString() + "\n\nRating: " + ratingText + "\n\nCustom feedback:\n" + userFeedback;

        String[] TO = {getString(R.string.email_to)}; // 使用字符串资源替换电子邮件地址
        String subject = getString(R.string.email_subject);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            // 使用 sendEmailLauncher 启动电子邮件客户端
            sendEmailLauncher.launch(Intent.createChooser(emailIntent, getString(R.string.send_feedback)));
            Toast.makeText(Feedback.this, getString(R.string.reminder_send_email), Toast.LENGTH_LONG).show(); // 添加这一行
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Feedback.this, getString(R.string.email_client_not_installed), Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your message has been sent Successfully!");

        builder.setPositiveButton("OK", (dialog, id) -> {
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

            int buttonWidth = 180;
            LinearLayout.LayoutParams positiveButtonLayoutParams = new LinearLayout.LayoutParams(buttonWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

            int buttonMargin = 16;
            positiveButtonLayoutParams.setMargins(buttonMargin, 0, buttonMargin, 0);

            positiveButton.setLayoutParams(positiveButtonLayoutParams);
            positiveButton.setTextColor(Color.WHITE);

            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(Color.BLUE);
        });

        alertDialog.show();
    }
}

