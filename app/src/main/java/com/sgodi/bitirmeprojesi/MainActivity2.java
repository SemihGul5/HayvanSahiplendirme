package com.sgodi.bitirmeprojesi;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.sgodi.bitirmeprojesi.databinding.ActivityMain2Binding;
import com.sgodi.bitirmeprojesi.fragments.AnaSayfaFragment;
import com.sgodi.bitirmeprojesi.fragments.AyarlarFragment;
import com.sgodi.bitirmeprojesi.fragments.SahiplenFragment;

public class MainActivity2 extends AppCompatActivity {
    private ActivityMain2Binding binding;
    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragmentManager =getSupportFragmentManager();


        NavHostFragment navHostFragment=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
        NavigationUI.setupWithNavController(binding.bottomNavigationView,navHostFragment.getNavController());



    }

}