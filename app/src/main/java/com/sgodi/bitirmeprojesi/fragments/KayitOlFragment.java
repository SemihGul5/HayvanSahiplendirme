package com.sgodi.bitirmeprojesi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentKayitOlBinding;

public class KayitOlFragment extends Fragment {
    private FragmentKayitOlBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentKayitOlBinding.inflate(inflater, container, false);


        //Giriş yap ekranına gidiş
        binding.kayitOlGirisYapButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_kayitOlFragment_to_girisYapFragment);
        });





        return binding.getRoot();
    }
}