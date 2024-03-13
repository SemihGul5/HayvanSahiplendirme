package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentEkleEvcilBinding;

public class EkleEvcilFragment extends Fragment {
    private FragmentEkleEvcilBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentEkleEvcilBinding.inflate(inflater, container, false);
        binding.toolbarHayvanEkle.setTitle("Hayvan Ekle");



        return binding.getRoot();
    }
}