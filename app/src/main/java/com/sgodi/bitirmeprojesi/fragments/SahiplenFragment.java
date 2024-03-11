package com.sgodi.bitirmeprojesi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentSahiplenBinding;

public class SahiplenFragment extends Fragment {
    private FragmentSahiplenBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentSahiplenBinding.inflate(inflater, container, false);











        return binding.getRoot();
    }
}