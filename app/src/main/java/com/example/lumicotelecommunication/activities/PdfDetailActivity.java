package com.example.lumicotelecommunication.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lumicotelecommunication.MyApplication;
import com.example.lumicotelecommunication.databinding.ActivityPdfDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PdfDetailActivity extends AppCompatActivity {

    private ActivityPdfDetailBinding binding;
    String surveyId, surveyTittle, surveyUrl;

    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        surveyId = intent.getStringExtra("surveyId");

        binding.btnDownload.setVisibility(View.GONE);

        loadSurveyDetail();

        MyApplication.incrementSurveyViewCount(surveyId);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnLihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(PdfDetailActivity.this, PdfViewActivity.class);
                intent1.putExtra("surveyId", surveyId);
                startActivity(intent1);
            }
        });

        binding.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG_DOWNLOAD, "onClick: Checking Permission");
                if (ContextCompat.checkSelfPermission(PdfDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG_DOWNLOAD, "onClick: Permission already granted, can download file");
                    MyApplication.downloadSuvey(PdfDetailActivity.this, ""+surveyId, ""+surveyTittle, ""+surveyUrl);

                }
                else {
                    Log.d(TAG_DOWNLOAD, "onClick: Permission was not granted, request permission...");
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
               if (isGranted) {
                   Log.d(TAG_DOWNLOAD, " Permission Granted");
                   MyApplication.downloadSuvey(this, ""+surveyId, ""+surveyTittle, ""+surveyUrl);
               }
               else {
                   Log.d(TAG_DOWNLOAD, "Permission was denied...");
                   Toast.makeText(this, "Permission was denied...", Toast.LENGTH_SHORT).show();
               }
            });

    private void loadSurveyDetail() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys");
        ref.child(surveyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        surveyTittle = ""+snapshot.child("name_site").getValue();
                        String tglSurvei = ""+snapshot.child("tanggal_survei").getValue();
                        String projectId = ""+snapshot.child("projectId").getValue();
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        String downloadCount = ""+snapshot.child("downloadCount").getValue();
                        surveyUrl = ""+snapshot.child("url").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();

                        binding.btnDownload.setVisibility(View.VISIBLE);

                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));
                        MyApplication.loadProject(
                                ""+projectId,
                                binding.projectTv
                        );
                        MyApplication.loadPdfFromUrlSingePage(
                                ""+surveyUrl,
                                ""+surveyTittle,
                                binding.pdfView,
                                binding.progressBar
                        );
                        MyApplication.loadPdfSize(
                                ""+surveyUrl,
                                ""+surveyTittle,
                                binding.sizeTv
                        );

                        binding.siteName.setText(surveyTittle);
                        binding.tglSrv.setText(tglSurvei);
                        binding.viewsTv.setText(viewsCount.replace("null","N/A"));
                        binding.downloadsTv.setText(downloadCount.replace("null", "N/A"));
                        binding.dateTv.setText(date);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}