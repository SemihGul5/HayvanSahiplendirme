package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentHayvanimKisilikYonergeleriBinding;

public class HayvanimKisilikYonergeleriFragment extends Fragment {
    private FragmentHayvanimKisilikYonergeleriBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHayvanimKisilikYonergeleriBinding.inflate(inflater, container, false);






        return binding.getRoot();
    }
}