package com.example.lumicotelecommunication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lumicotelecommunication.databinding.ActivityPdfEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfEditActivity extends AppCompatActivity {

    private ActivityPdfEditBinding binding;

    //survey id get from intent started AdapterPdfAdmin
    private String surveyId;

    private ProgressDialog progressDialog;

    private ArrayList<String> projectTittleArrayList, projectIdArrayList;

    private static final String TAG = "BOOK_EDIT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        surveyId = getIntent().getStringExtra("surveyId");

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        
        loadProjects();
        loadSurveyInfo();

        binding.projectTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                projectDialog();
            }
        });
        
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();

            }
        });

    }


    private void loadSurveyInfo() {
        Log.d(TAG, "loadSurveyInfo: Loading survey info");

        DatabaseReference refSurveys = FirebaseDatabase.getInstance().getReference("Surveys");
        refSurveys.child(surveyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get info survey
                        selectedProjectId = ""+snapshot.child("projectId").getValue();
                        String tanggalSurvei = ""+snapshot.child("tanggal_survei").getValue();
                        String siteName = ""+snapshot.child("name_site").getValue();
                        //set to views
                        binding.siteName.setText(siteName);
                        binding.tglEt.setText(tanggalSurvei);

                        Log.d(TAG, "onDataChange: Loading Project Surveys info");
                        DatabaseReference refSurveyProject = FirebaseDatabase.getInstance().getReference("Project");
                        refSurveyProject.child(selectedProjectId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        //get project
                                        String project = ""+snapshot.child("project").getValue();
                                        //set to project text view
                                        binding.projectTv.setText(project);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private String siteName = "", tanggalSurvei ="";
    private void validateData() {
        //get data
        siteName = binding.siteName.getText().toString().trim();
        tanggalSurvei = binding.tglEt.getText().toString().trim();

        //validation data
        if (TextUtils.isEmpty(siteName)) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(tanggalSurvei)) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedProjectId)){

        }
        else {
            updatePdf();
        }

    }

    private void updatePdf() {
        Log.d(TAG, "updatePdf: Starting updating pdf info to db...");

        //show progress
        progressDialog.setMessage("Updating survey info...");
        progressDialog.show();

        //setup update data to db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name_site", ""+siteName);
        hashMap.put("tanggal_survei", ""+tanggalSurvei);
        hashMap.put("projectId", ""+selectedProjectId);

        //start update
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys");
        ref.child(surveyId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Survey updated");
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, "Survey info updated...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed to update due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String selectedProjectId, selectedProjectTittle;

    private void projectDialog() {
        String[] projectsArray = new String[projectTittleArrayList.size()];
        for (int i=0; i<projectTittleArrayList.size(); i++){
            projectsArray[i] = projectTittleArrayList.get(i);
        }


        //alert dialog
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Pilih Project")
                .setItems(projectsArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedProjectId = projectIdArrayList.get(i);
                        selectedProjectTittle = projectTittleArrayList.get(i);

                        //set to text view
                        binding.projectTv.setText(selectedProjectTittle);
                    }
                })
                .show();
    }

    private void loadProjects() {
        Log.d(TAG, "loadProjects: Loading Project...");

        projectIdArrayList = new ArrayList<>();
        projectTittleArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Project");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                projectIdArrayList.clear();
                projectTittleArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String id = ""+ds.child("id").getValue();
                    String project = ""+ds.child("project").getValue();
                    projectIdArrayList.add(id);
                    projectTittleArrayList.add(project);

                    Log.d(TAG, "onDataChange: ID:" +id);
                    Log.d(TAG, "onDataChange: Project:" +project);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}