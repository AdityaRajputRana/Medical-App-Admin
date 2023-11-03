package com.example.medicalappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.medicalappadmin.databinding.ActivityDashboardBinding;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setItemIconTintList(null);
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){

                }
                else if(item.getItemId() == R.id.notepad){

                }
                else if(item.getItemId() == R.id.tab3){

                }
                else if(item.getItemId() == R.id.profile){

                }
                return true;
            }
        });
    }
    private void changeFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment);
        fragmentTransaction.commit();
    }
}