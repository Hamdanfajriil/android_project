package com.example.lumicotelecommunication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.lumicotelecommunication.activities.DashboardAdminActivity;
import com.example.lumicotelecommunication.activities.LoginActivity;
import com.example.lumicotelecommunication.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashSrceen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_srceen);

        firebaseAuth = FirebaseAuth.getInstance();
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        },2000);
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(SplashSrceen.this, LoginActivity.class));
            finish();
        }
        else {
            DatabaseReference fd = FirebaseDatabase.getInstance().getReference("Users");
            fd.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            //get userType
                            String userType = ""+snapshot.child("userType").getValue();
                            if (userType.equals("teknisi")) {
                                startActivity(new Intent(SplashSrceen.this, DashboardAdminActivity.class));
                                finish();
                            }
                            else if (userType.equals("admin")) {
                                startActivity(new Intent(SplashSrceen.this, MainActivity.class));
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
        }
    }
}