package com.android.noteapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.noteapp.databinding.ActivityGlobalPdfBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class GlobalPDF extends BaseDrawerActivity implements PdfAdapter.OnItemClickListener{

    ActivityGlobalPdfBinding activityGlobalPdfBinding;
    FloatingActionButton addNoteButton;
    RecyclerView recyclerView;
    PdfAdapter pdfAdapter;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ArrayList<Pdf> pdfFiles;
    BottomNavigationView bottomNavigationView;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGlobalPdfBinding = ActivityGlobalPdfBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //去掉ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(activityGlobalPdfBinding.getRoot());

        addNoteButton = activityGlobalPdfBinding.btnAddNote;

        addNoteButton.setOnClickListener(v -> {
            openUploadGlobalPdf();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        recyclerView = findViewById(R.id.recyclePdf);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.txtsearch);
        searchView.clearFocus();

        databaseReference = FirebaseDatabase.getInstance().getReference("pdfGlobal");
        pdfFiles = new ArrayList<>();
        pdfAdapter = new PdfAdapter(pdfFiles);
        recyclerView.setAdapter(pdfAdapter);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

/*
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setText("");
            }
        });

 */


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Pdf pdf = dataSnapshot.getValue(Pdf.class);
                    pdfFiles.add(pdf);
                }
                pdfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed To Load. Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        pdfAdapter = new PdfAdapter(pdfFiles);
        recyclerView.setAdapter(pdfAdapter);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
             int id = item.getItemId();
             if (id == R.id.MyFile){
                 Intent intent = new Intent(GlobalPDF.this, GlobalPDFMyFiles.class);
                 startActivity(intent);
                 return true;
             }else {
                 return GlobalPDF.super.onNavigationItemSelected(item);
             }
        });



        // Load pdf files from Firebase Storage
        loadPdfFiles();
    }

    public void searchList(String text){
        ArrayList<Pdf> searchList = new ArrayList<>();
        for (Pdf pdf: pdfFiles){
            if (pdf.getFileName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(pdf);
            }
        }
        pdfAdapter.searchDataList(searchList);
    }

    private void openUploadGlobalPdf() {
        Intent intent = new Intent(this, UploadGlobalPdf.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    private void loadPdfFiles() {
        // Replace "pdfs" with the appropriate storage reference path to your PDF files
        StorageReference pdfsRef = storageReference.child("pdfs");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> pdfFiles = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Pdf pdf = dataSnapshot.getValue(Pdf.class);
                }
                // Update the adapter with the pdfFiles list
                pdfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed To Load. Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });

        /*
        pdfsRef.listAll().addOnSuccessListener(listResult -> {
            ArrayList<Pdf> pdfFiles = new ArrayList<>();
            for (StorageReference item : listResult.getItems()) {
                // Get name of the PDF file and add to pdfFiles list
            }
            // Update the adapter with the pdfFiles list
            pdfAdapter.updatePdfFiles(pdfFiles);
        }).addOnFailureListener(e -> {
            // Handle any errors that may occur while listing PDF files
        });

         */
    }


    @Override
    public void onItemClick(View view, String fileName) {
        // Open the selected PDF file for reading
        StorageReference pdfRef = storageReference.child("pdfs").child(fileName);
        pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String pdfUrl = uri.toString();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "File Open Failed. Please Try Again", Toast.LENGTH_SHORT).show());
    }
}