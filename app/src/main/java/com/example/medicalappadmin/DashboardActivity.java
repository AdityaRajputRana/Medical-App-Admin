package com.example.medicalappadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.medicalappadmin.databinding.ActivityDashboardBinding;
import com.example.medicalappadmin.fragments.HomeFragment;
import com.example.medicalappadmin.fragments.NotepadFragment;
import com.example.medicalappadmin.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationBarView;

public class DashboardActivity extends AppCompatActivity {
    private ActivityDashboardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setItemIconTintList(null);
        changeFragment(0);
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.home){
                    changeFragment(0);
                }
                else if(item.getItemId() == R.id.profile){
                    changeFragment(1);
                }
                return true;
            }
        });
    }

    private final HomeFragment homeFragment = new HomeFragment();
    private final  ProfileFragment profileFragment = new ProfileFragment();
    private void changeFragment(int i){
        Fragment fragment = homeFragment;
        switch (i){
            case 1:
                fragment = profileFragment;
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment);
        fragmentTransaction.commit();
    }

}