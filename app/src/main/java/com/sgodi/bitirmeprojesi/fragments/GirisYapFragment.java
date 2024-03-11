package com.sgodi.bitirmeprojesi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentGirisYapBinding;


public class GirisYapFragment extends Fragment {
    private FragmentGirisYapBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentGirisYapBinding.inflate(inflater, container, false);



        //Kayıt Ol sayfasına gidiş
        binding.kayitOlButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_girisYapFragment_to_kayitOlFragment);
        });








        return binding.getRoot();
    }
}