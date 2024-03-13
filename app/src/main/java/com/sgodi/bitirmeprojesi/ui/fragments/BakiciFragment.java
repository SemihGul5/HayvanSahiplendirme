package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentBakiciBinding;

public class BakiciFragment extends Fragment {
    private FragmentBakiciBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentBakiciBinding.inflate(inflater, container, false);
        binding.toolbar2.setTitle("Bakıcılar");




        return binding.getRoot();
    }
}