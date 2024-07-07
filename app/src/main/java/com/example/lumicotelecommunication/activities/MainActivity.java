package com.example.lumicotelecommunication.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.lumicotelecommunication.R;
import com.example.lumicotelecommunication.SurveyUserFragment;
import com.example.lumicotelecommunication.databinding.ActivityMainBinding;
import com.example.lumicotelecommunication.models.ModelProject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ArrayList<ModelProject> projectArrayList;
    public ViewPagerAdapter viewPagerAdapter;

    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfilActivity.class));
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        setupViewPagerAdapter(binding.viewPager);
        binding.tableLayout.setupWithViewPager(binding.viewPager);

        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

    }

    private void setupViewPagerAdapter(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);

        projectArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Project");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                projectArrayList.clear();

                //add data to models
                ModelProject modelAll = new ModelProject("01", "All", "", 1);
                ModelProject modelMostViewed = new ModelProject("02", "Most Viewed", "", 1);
                ModelProject modelMostDownloaded = new ModelProject("03", "Most Downloaded", "", 1);

                //add models to list
                projectArrayList.add(modelAll);
                projectArrayList.add(modelMostViewed);
                projectArrayList.add(modelMostDownloaded);
                //add data to view pager adapter
                viewPagerAdapter.addFragment(SurveyUserFragment.newInstance(
                        ""+modelAll.getId(),
                        ""+modelAll.getProject(),
                        ""+modelAll.getUid()
                ), modelAll.getProject());
                viewPagerAdapter.addFragment(SurveyUserFragment.newInstance(
                        ""+modelMostViewed.getId(),
                        ""+modelMostViewed.getProject(),
                        ""+modelMostViewed.getUid()
                ), modelMostViewed.getProject());
                viewPagerAdapter.addFragment(SurveyUserFragment.newInstance(
                        ""+modelMostDownloaded.getId(),
                        ""+modelMostDownloaded.getProject(),
                        ""+modelMostDownloaded.getUid()
                ), modelMostDownloaded.getProject());
                //refresh list
                viewPagerAdapter.notifyDataSetChanged();

                //load from firebase
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelProject model = ds.getValue(ModelProject.class);
                    projectArrayList.add(model);
                    viewPagerAdapter.addFragment(SurveyUserFragment.newInstance(
                            ""+model.getId(),
                            ""+model.getProject(),
                            ""+model.getUid()), model.getProject());
                    viewPagerAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        viewPager.setAdapter(viewPagerAdapter);

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<SurveyUserFragment> fragmentsList = new ArrayList<>();
        private ArrayList<String> fragmentTittleList = new ArrayList<>();
        private Context context;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
            super(fm, behavior);
            this.context = context;

        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        private void addFragment(SurveyUserFragment fragment, String tittle){
            fragmentsList.add(fragment);
            fragmentTittleList.add(tittle);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTittleList.get(position);
        }
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

                            binding.textName.setText(name);
                            binding.welcome.setText(userType);

                            Glide.with(MainActivity.this)
                                    .load(profileImage)
                                    .circleCrop()
                                    .into(binding.btnProfile);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}