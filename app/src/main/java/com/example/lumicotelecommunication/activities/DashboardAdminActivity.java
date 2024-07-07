package com.example.lumicotelecommunication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.lumicotelecommunication.adapters.AdapterProject;
import com.example.lumicotelecommunication.databinding.ActivityDashboardAdminBinding;
import com.example.lumicotelecommunication.models.ModelProject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardAdminActivity extends AppCompatActivity {

    private ActivityDashboardAdminBinding binding;
    private FirebaseAuth firebaseAuth;

    //Arraylist
    private ArrayList<ModelProject> projectArrayList;
    private AdapterProject adapterProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadProject();

        binding.btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAdminActivity.this, ProfilActivity.class));
            }
        });

        //search project in application
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                try {
                    adapterProject.getFilter().filter(s);
                }
                catch (Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //logout dari aplikasi
        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        binding.btnProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAdminActivity.this, AddProjectActivity.class));
                finish();
            }
        });

        binding.addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAdminActivity.this, AddPdfActivity.class));
                finish();
            }
        });

    }

    private void loadProject() {
        //init arraylist
        projectArrayList = new ArrayList<>();

        //get all project from database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Project");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ModelProject model = ds.getValue(ModelProject.class);
                    //add to arraylist
                    projectArrayList.add(model);
                }
                //setup
                adapterProject = new AdapterProject(DashboardAdminActivity.this, projectArrayList);
                binding.recView.setAdapter(adapterProject);
                adapterProject.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser==null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = ""+snapshot.child("nama").getValue();
                            String userType = ""+snapshot.child("userType").getValue();
                            String profileImage = ""+snapshot.child("profileImage").getValue();

                            binding.Textname.setText(name);
                            binding.welcome.setText(userType);

                            Glide.with(DashboardAdminActivity.this)
                                    .load(profileImage)
                                    .circleCrop()
                                    .into(binding.btnProfil);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

//            String email = firebaseUser.getEmail();
//            binding.Textname.setText(email);
        }
    }
}