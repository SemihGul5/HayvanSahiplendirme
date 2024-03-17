package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentBizeUlasBinding;

public class BizeUlasFragment extends Fragment {
    private FragmentBizeUlasBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentBizeUlasBinding.inflate(inflater, container, false);
        binding.materialToolbarBizeUlas.setTitle("Bize Ulaşın");




        return binding.getRoot();
    }
}