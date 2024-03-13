package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentHayvanlarimBinding;

public class HayvanlarimFragment extends Fragment {
    private FragmentHayvanlarimBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHayvanlarimBinding.inflate(inflater, container, false);
        binding.toolbarHayvanlarim.setTitle("Hayvanlarım");

        binding.floatingActionButtonHayvanlarim.setOnClickListener(view -> {
            //hayvan ekleye git
            Navigation.findNavController(view).navigate(R.id.action_hayvanlarimFragment_to_ekleEvcilFragment);
        });


        return binding.getRoot();
    }
}