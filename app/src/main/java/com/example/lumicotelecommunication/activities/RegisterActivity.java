package com.example.lumicotelecommunication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.lumicotelecommunication.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private ActivityRegisterBinding binding;
    private ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        //Progres Dialog di dalam Aplikasi Android
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);

        binding.textlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.btDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatedata();
            }
        });

    }

    private  String name ="", email = "", domisili ="", userType ="", pass ="";
    private void validatedata() {

        //get data
        name = binding.fName.getText().toString();
        email = binding.femail.getText().toString();
        domisili = binding.fDomisili.getText().toString();
        userType = binding.fUser.getText().toString();
        pass = binding.fPws.getText().toString();
        String konfpass = binding.cPass.getText().toString();

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Masukan nama anda...", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email tidak valid...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(domisili)) {
            Toast.makeText(this, "Masukan posisi anda di perusahaan...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Masukan password...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(konfpass)) {
            Toast.makeText(this, "Konfirmasi passowrd...", Toast.LENGTH_SHORT).show();
        }
        else if (!pass.equals(konfpass)) {
            Toast.makeText(this, "Password tidak cocok...", Toast.LENGTH_SHORT).show();
        }
        else {
            createUser();
        }
    }

    private void createUser() {
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        //create akun
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUser() {

        progressDialog.setMessage("Saving user info...");
        long timestamp = System.currentTimeMillis();

        //get user by UID
        String uid = mAuth.getUid();

        //setup data user ke dalam firebase
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("nama", name);
        hashMap.put("email", email);
        hashMap.put("domisili", domisili);
        hashMap.put("profileImage", "");
        hashMap.put("userType", userType);
        hashMap.put("timestamp", timestamp);

        //set data ke database
        DatabaseReference fd = FirebaseDatabase.getInstance().getReference("Users");
        fd.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //data berhasil ke database
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Akun Berhasil dibuat...",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }
}