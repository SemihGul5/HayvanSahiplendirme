package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentBakiciOlBinding;


public class BakiciOlFragment extends Fragment {
    private FragmentBakiciOlBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentBakiciOlBinding.inflate(inflater, container, false);
        binding.materialToolbarBakiciOl.setTitle("Bakıcı Ol");// kişilik testinden çıkan sonuca göre olabilir veya olamaz...










        return binding.getRoot();
    }
}