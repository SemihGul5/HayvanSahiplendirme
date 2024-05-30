package com.sgodi.bitirmeprojesi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sgodi.bitirmeprojesi.databinding.ActivityMain2Binding;
import com.sgodi.bitirmeprojesi.ui.fragments.SahiplenFragment;

public class MainActivity2 extends AppCompatActivity {
    private ActivityMain2Binding binding;
    FragmentManager fragmentManager;
    private NavController navController;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragmentManager =getSupportFragmentManager();


        /*NavHostFragment navHostFragment=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        NavigationUI.setupWithNavController(binding.bottomNavigationView,navHostFragment.getNavController());*/
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.anaSayfaFragment){
                    navController.navigate(R.id.anaSayfaFragment);
                } else if (item.getItemId()==R.id.sahiplenFragment) {
                    navController.navigate(R.id.sahiplenFragment);
                } else if (item.getItemId()==R.id.profilGorunumFragment) {
                    navController.navigate(R.id.profilGorunumFragment);
                }
                return false;
            }
        });


        

    }

}