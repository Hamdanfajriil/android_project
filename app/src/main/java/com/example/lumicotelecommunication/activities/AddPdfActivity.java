package com.example.lumicotelecommunication.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lumicotelecommunication.databinding.ActivityAddPdfBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPdfActivity extends AppCompatActivity {

    private ActivityAddPdfBinding binding;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "ADD_PDF_TAG";
    private Uri pdfUri = null;
    private static final int PDF_PICK_CODE = 1000;
    private ArrayList<String> projectTittleArrayList, projectIdArrayList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfProject();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddPdfActivity.this, DashboardAdminActivity.class));
                finish();
            }
        });

        //attach file pdf
        binding.btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        binding.projectTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                projectTvDialog();
            }
        });

        binding.btnUploud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }


    private void loadPdfProject() {
        Log.d(TAG, "loadPdfProject: Loading pdf project....");
        projectTittleArrayList = new ArrayList<>();
        projectIdArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Project");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                projectTittleArrayList.clear();
                projectIdArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String projectId = ""+ds.child("id").getValue();
                    String projectTittle = ""+ds.child("project").getValue();

                    projectTittleArrayList.add(projectTittle);
                    projectIdArrayList.add(projectId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private String name_site = "", tanggal_survei = "";

    private void validateData() {

        name_site = binding.siteName.getText().toString().trim();
        tanggal_survei = binding.tglEt.getText().toString().trim();


        //validasi data
        if (TextUtils.isEmpty(name_site)){
            Toast.makeText(this, "Masukan Nama Site....", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(tanggal_survei)) {
            Toast.makeText(this, "Masukan Tanggal Survei...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedProductTittle)) {
            Toast.makeText(this, "Pilih Project...", Toast.LENGTH_SHORT).show();
        }
        else if (pdfUri==null) {
            Toast.makeText(this, "Pilih File Pdf...", Toast.LENGTH_SHORT).show();
        }
        else {
            uploadPdfToStorage();
        }

    }

    private void uploadPdfToStorage() {

        Log.d(TAG, "uploudPdfToStorage: uplouding to storage...");

        progressDialog.setMessage("uplouding pdf...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        String filePathAndName = "Surveys/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: PDF uploaded to storage...");
                        Log.d(TAG, "onSuccess: getting pdf url");

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedPdfUrl = ""+uriTask.getResult();
                        uploadPdfInfoToDb(uploadedPdfUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: PDF upload failed to "+e.getMessage());
                        Toast.makeText(AddPdfActivity.this, "PDF upload failed to due "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPdfInfoToDb(String uploadedPdfUrl, long timestamp) {
        Log.d(TAG, "uploadPdfToStorage: uploading to firebase db...");

        progressDialog.setMessage("Uploading pdf info...");

        String uid = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+uid);
        hashMap.put("id", ""+timestamp);
        hashMap.put("name_site", ""+name_site);
        hashMap.put("tanggal_survei", ""+tanggal_survei);
        hashMap.put("projectId", ""+selectedProductId);
        hashMap.put("url", ""+uploadedPdfUrl);
        hashMap.put("timestamp", timestamp);
        hashMap.put("viewsCount", 0);
        hashMap.put("downloadCount", 0);

        //DB > Project
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Successfully uploaded...");
                        Toast.makeText(AddPdfActivity.this, "Successfully uploaded...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed to upload to db due to "+e.getMessage());
                        Toast.makeText(AddPdfActivity.this, "Failed to upload to db due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String selectedProductId, selectedProductTittle;

    private void projectTvDialog() {
        Log.d(TAG, "projectPickDialog: showing project pick dialog");

        String [] projectArray = new String[projectTittleArrayList.size()];
        for (int i = 0; i< projectTittleArrayList.size(); i++){
            projectArray[i] = projectTittleArrayList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Project")
                .setItems(projectArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        selectedProductTittle = projectTittleArrayList.get(i);
                        selectedProductId = projectIdArrayList.get(i);

                        binding.projectTv.setText(selectedProductTittle);

                        Log.d(TAG, "onClick: Selected Project: "+selectedProductId+" "+selectedProductTittle);
                    }
                })
                .show();
    }

    private void pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent");

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PDF_PICK_CODE) {
                Log.d(TAG, "onActivityResult: PDF Picked");

                pdfUri = data.getData();
                Log.d(TAG, "onActivityResult: URI: "+pdfUri);
            }
        }
        else {
            Log.d(TAG, "onActivityResult: cancelled picking pdf");
            Toast.makeText(this, "cancelled picking pdf", Toast.LENGTH_SHORT).show();

        }
    }
}