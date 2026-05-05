package com.android.noteapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.android.noteapp.databinding.ActivityUploadLocalBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalPDF extends BaseDrawerActivity {
    Button localPdf;

    private RelativeLayout uploadLocalProgressBar;
    private ProgressBar progressBar;

    private TextView progressText;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @SuppressLint("SetTextI18n")
    private void showProgressBar() {
        uploadLocalProgressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);  // Add this line
        progressText.setText("Uploading PDF...");
    }

    private void hideProgressBar() {
        uploadLocalProgressBar.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        progressText.setVisibility(View.GONE);  // Add this line
        progressText.setText("");
    }

    private final ActivityResultLauncher<Intent> pdfActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri pdfUri = data.getData();

                        showProgressBar();

                        executorService.execute(() -> {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            handler.post(() -> {
                                hideProgressBar();
                                Toast.makeText(LocalPDF.this, "PDF upload completed successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LocalPDF.this, PdfEditActivity.class);
                                intent.putExtra("pdfUri", pdfUri.toString());
                                startActivity(intent);
                            });
                        });
                    }
                } else {
                    hideProgressBar();
                    Toast.makeText(LocalPDF.this, "PDF uploaded failed", Toast.LENGTH_SHORT).show();
                }
            });

    ActivityUploadLocalBinding activityUploadBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUploadBinding = ActivityUploadLocalBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(activityUploadBinding.getRoot());

        uploadLocalProgressBar = findViewById(R.id.upload_local_progress_bar);
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);

        localPdf = findViewById(R.id.upload_pdf);
        localPdf.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            pdfActivityResultLauncher.launch(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
